document.addEventListener('submit', (e) => {
  const form = e.target;
  const btn = form.querySelector('button[type=submit]');
  if (btn) {
    btn.disabled = true;
    btn.classList.add('opacity-60');
  }
});

window.copyApplicationId = (id) => {
  if (!id) return;
  navigator.clipboard.writeText(id).then(() => {
    alert('신청 ID가 복사되었습니다.');
  });
};
