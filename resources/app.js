document.getElementById('peopleMenu').addEventListener('click', function () {
    var menu = document.getElementById('dropdownMenu');
    menu.classList.add('active');
});

document.getElementById('closeMenu').addEventListener('click', function () {
    var menu = document.getElementById('dropdownMenu');
    menu.classList.remove('active');
});