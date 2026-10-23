"""真实 HTTP 验证；仅使用 Python 标准库。"""
import http.cookiejar
import json
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from datetime import datetime, timedelta, timezone

BASE = (sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8080").rstrip("/")


class Client:
    def __init__(self):
        self.http = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
        self.csrf = None

    def request(self, path, method="GET", data=None, status=200, form=False):
        headers = {}
        if method != "GET":
            if self.csrf is None:
                self.csrf = self.request("/api/auth/csrf")["data"]
            headers[self.csrf["headerName"]] = self.csrf["token"]
        body = None
        if data is not None:
            body = (urllib.parse.urlencode(data) if form else json.dumps(data)).encode()
            headers["Content-Type"] = "application/x-www-form-urlencoded" if form else "application/json"
        request = urllib.request.Request(BASE + path, data=body, method=method, headers=headers)
        try:
            response = self.http.open(request, timeout=15)
        except urllib.error.HTTPError as error:
            response = error
        payload = json.loads(response.read())
        assert response.status == status, (method, path, response.status, payload)
        assert payload["code"] == status, payload
        return payload

    def signup_login(self, username):
        credentials = {"username": username, "password": "password123"}
        self.request("/api/auth/register", "POST", credentials, 201)
        self.request("/api/auth/login", "POST", credentials, form=True)
        self.csrf = None  # 登录后令牌轮换
        assert self.request("/api/auth/me")["data"]["username"] == username


guest, owner, member = Client(), Client(), Client()
guest.request("/api/events")
guest.request("/api/auth/me", status=401)
suffix = str(time.time_ns())[-12:]
owner.signup_login("owner_" + suffix)
member.signup_login("member_" + suffix)
event = {
    "title": "HTTP smoke test",
    "description": "Smoke test of real sessions, CSRF and registration.",
    "location": "Online",
    "eventTime": (datetime.now(timezone.utc) + timedelta(days=2)).isoformat(),
    "capacity": 1,
}
created = owner.request("/api/events", "POST", event, 201)["data"]
path = "/api/events/" + str(created["id"])
member.request(path, "PUT", event, 403)
member.request(path + "/registrations", "POST", status=201)
member.request(path + "/registrations", "POST", status=409)
assert guest.request(path)["data"]["registeredCount"] == 1
owner.request(path, "DELETE")
assert guest.request(path)["data"]["cancelled"] is True
owner.request("/api/auth/logout", "POST")
owner.request("/api/auth/me", status=401)
print("PASS: real HTTP, CSRF, sessions, ownership, registration, duplicate prevention, cancellation, logout")
