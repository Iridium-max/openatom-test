'use strict';
const $ = s => document.querySelector(s);
let me = null, csrf = null, page = 0, totalPages = 0, editing = false, listRequest = 0;
const date = value => new Date(value).toLocaleString('zh-CN', {hour12:false});
const state = e => e.cancelled ? '已取消' : new Date(e.eventTime) > new Date() ? '即将开始' : '已结束';
function node(tag, text, className) { const e = document.createElement(tag); if (text !== undefined) e.textContent = text; if (className) e.className = className; return e; }
function message(text, error = false) { const box = $('#notice'); box.textContent = text; box.className = error ? 'error' : ''; box.hidden = false; }
async function fetchCsrf() { const r = await fetch('/api/auth/csrf'); const result = await r.json(); if (!r.ok) throw Error(result.message); csrf = result.data; }
async function api(url, options = {}) {
  const method = options.method || 'GET'; const headers = new Headers(options.headers || {});
  if (method !== 'GET') { if (!csrf) await fetchCsrf(); headers.set(csrf.headerName, csrf.token); }
  if (options.body && !(options.body instanceof URLSearchParams)) headers.set('Content-Type', 'application/json');
  const response = await fetch(url, {...options, headers, credentials:'same-origin'});
  const result = await response.json();
  if (!response.ok) { if (response.status === 401) { me = null; renderIdentity(); } if (response.status === 403) csrf = null; throw Error(result.message || '请求失败'); }
  return result.data;
}
function renderIdentity() { $('#identity').textContent = me ? me.username : '游客'; $('#loginOpen').hidden = !!me; $('#logout').hidden = !me; }
function requireLogin() { if (me) return true; $('#authError').textContent = ''; $('#authDialog').showModal(); return false; }
function button(text, handler, className = '') { const b = node('button', text, className); b.type = 'button'; b.addEventListener('click', handler); return b; }
async function load() {
  const request = ++listRequest; $('#events').replaceChildren(node('div', '正在加载…', 'empty'));
  const params = new URLSearchParams({page, size:9, keyword:$('#keyword').value, status:$('#status').value});
  try {
    const result = await api('/api/events?' + params); if (request !== listRequest) return;
    totalPages = result.totalPages; $('#resultCount').textContent = `共 ${result.totalElements} 场活动，找到你的下一次相遇`;
    if (page > 0 && page >= totalPages) { page = Math.max(0, totalPages - 1); return load(); }
    $('#events').replaceChildren();
    if (!result.content.length) $('#events').append(node('div', '还没有符合条件的活动\n换个关键词，或发起你的第一场活动。', 'empty'));
    for (const event of result.content) {
      const card = node('article', undefined, 'card');
      const top = node('div', undefined, 'cardTop'); top.append(node('span', `EVENT / ${String(event.id).padStart(3,'0')}`, 'number'), node('span', state(event), 'tag' + (state(event) === '即将开始' ? '' : ' ended')));
      const meta = node('div', undefined, 'meta'); meta.append(node('div', '◷ ' + date(event.eventTime)), node('div', '⌖ ' + event.location));
      const bottom = node('div', undefined, 'cardBottom'); bottom.append(node('span', `${event.registeredCount} / ${event.capacity} 人已报名`, 'capacity'), button('查看详情 ↗', () => detail(event.id)));
      card.append(top, node('h3', event.title), node('p', event.description, 'excerpt'), meta, bottom); $('#events').append(card);
    }
    $('#pageInfo').textContent = `第 ${totalPages ? page + 1 : 0} / ${totalPages} 页`;
    $('#prev').disabled = page === 0; $('#next').disabled = page + 1 >= totalPages;
  } catch (e) { if (request !== listRequest) return; $('#events').replaceChildren(node('div', '活动加载失败，请稍后重试。', 'empty')); message(e.message, true); }
}
async function detail(id) {
  try {
    const e = await api('/api/events/' + id), content = $('#detailContent'); content.replaceChildren();
    const info = node('div', undefined, 'detailInfo'); info.append(node('div', '时间：' + date(e.eventTime)), node('div', '地点：' + e.location), node('div', `发起人：${e.organizerName} · ${e.registeredCount} / ${e.capacity} 人报名`), node('div', '状态：' + state(e)));
    const actions = node('div', undefined, 'actions');
    if (me && me.id === e.organizerId) {
      const edit = button('修改', () => { $('#detailDialog').close(); openEditor(e); }, 'outline'); edit.disabled = state(e) !== '即将开始';
      const cancel = button('取消活动', async () => {
        if (!confirm('确定取消这场活动？已有报名记录将保留。')) return;
        cancel.disabled = true;
        try { await api('/api/events/' + id, {method:'DELETE'}); $('#detailDialog').close(); message('活动已取消'); await load(); }
        catch (err) { message(err.message, true); } finally { cancel.disabled = false; }
      }, 'outline'); cancel.disabled = e.cancelled; actions.append(edit, cancel);
    }
    const join = button('立即报名', async () => {
      if (!requireLogin()) { $('#detailDialog').close(); return; } join.disabled = true;
      try { await api(`/api/events/${id}/registrations`, {method:'POST'}); $('#detailDialog').close(); message('报名成功！'); await load(); }
      catch (err) { message(err.message, true); join.disabled = false; }
    }); join.disabled = state(e) !== '即将开始' || e.registeredCount >= e.capacity;
    actions.append(join); content.append(node('h2', e.title), info, node('p', e.description, 'description'), actions);
    $('#detailDialog').showModal();
  } catch (err) { message(err.message, true); }
}
function openEditor(e) {
  if (!requireLogin()) return;
  const form = $('#eventForm'); form.reset(); editing = !!e; $('#editorTitle').textContent = e ? '修改活动' : '发起活动'; $('#eventError').textContent = '';
  form.elements.id.value = e ? e.id : '';
  if (e) {
    for (const key of ['title','description','location','capacity']) form.elements[key].value = e[key];
    const d = new Date(e.eventTime); form.elements.eventTime.value = new Date(d.getTime() - d.getTimezoneOffset()*60000).toISOString().slice(0,16);
  }
  $('#eventDialog').showModal();
}
document.querySelectorAll('[data-close]').forEach(b => b.addEventListener('click', () => document.getElementById(b.dataset.close).close()));
$('#loginOpen').onclick = () => { $('#authError').textContent = ''; $('#authDialog').showModal(); };
$('#createOpen').onclick = () => openEditor();
$('#authForm').addEventListener('submit', async ev => {
  ev.preventDefault(); const form = ev.currentTarget, data = Object.fromEntries(new FormData(form));
  const buttons = [...form.querySelectorAll('button[type=submit]')]; buttons.forEach(b => b.disabled = true); $('#authError').textContent = '';
  try {
    if (ev.submitter?.value === 'register') await api('/api/auth/register', {method:'POST', body:JSON.stringify(data)});
    me = await api('/api/auth/login', {method:'POST', body:new URLSearchParams(data)});
    csrf = null; await fetchCsrf(); renderIdentity(); $('#authDialog').close(); form.reset(); message('登录成功'); await load();
  } catch (e) { $('#authError').textContent = e.message; } finally { buttons.forEach(b => b.disabled = false); }
});
$('#logout').onclick = async () => { try { await api('/api/auth/logout', {method:'POST'}); me = null; csrf = null; renderIdentity(); message('已退出登录'); await load(); } catch (e) { message(e.message,true); } };
$('#eventForm').addEventListener('submit', async ev => {
  ev.preventDefault(); const form = ev.currentTarget, data = Object.fromEntries(new FormData(form)); const id = data.id; delete data.id;
  data.capacity = Number(data.capacity); data.eventTime = new Date(data.eventTime).toISOString(); const submit = form.querySelector('button[type=submit]'); submit.disabled = true;
  try { await api('/api/events' + (editing ? '/' + id : ''), {method:editing ? 'PUT' : 'POST', body:JSON.stringify(data)}); $('#eventDialog').close(); page = 0; message('活动已保存'); await load(); }
  catch (e) { $('#eventError').textContent = e.message; } finally { submit.disabled = false; }
});
$('#filters').onsubmit = ev => { ev.preventDefault(); page = 0; load(); };
$('#prev').onclick = () => { if (page > 0) { page--; load(); } };
$('#next').onclick = () => { if (page + 1 < totalPages) { page++; load(); } };
(async () => { try { await fetchCsrf(); me = await api('/api/auth/me'); } catch (e) { /* 游客模式可正常浏览 */ } renderIdentity(); await load(); })();
