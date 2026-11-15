(function () {
    const MAX_FILES = 10;
    const MAX_SIZE = 10 * 1024 * 1024; // keep in sync with spring.servlet.multipart.max-file-size
    const input = document.getElementById('imageUpload');
    const grid = document.getElementById('photo-grid');
    const errorEl = document.getElementById('photo-error');

    let files = [];

    const sameFile = (a, b) =>
        a.name === b.name && a.size === b.size && a.lastModified === b.lastModified;

    // keep the real <input> in sync so the files are submitted with the form
    function syncInput() {
        const dt = new DataTransfer();
        files.forEach(f => dt.items.add(f));
        input.files = dt.files;
    }

    function render() {
        grid.innerHTML = '';
        files.forEach((file, index) => {
            const url = URL.createObjectURL(file);

            const col = document.createElement('div');
            col.className = 'col-3';
            col.innerHTML = `
            <div class="border rounded p-1 bg-light h-100 position-relative">
                ${index === 0 ? '<span class="badge text-bg-primary position-absolute top-0 start-0 m-1">Cover</span>' : ''}
                <img src="${url}" alt="" class="img-fluid rounded mb-1"
                     style="height: 100px; width: 100%; object-fit: cover;">
                <div class="small text-truncate mb-1" style="font-size: 0.75rem;"></div>
                <button type="button" class="btn btn-outline-danger w-100 py-0" style="font-size: 0.75rem;">Remove</button>
            </div>`;

            col.querySelector('.text-truncate').textContent = file.name;
            col.querySelector('img').onload = () => URL.revokeObjectURL(url);
            col.querySelector('button').addEventListener('click', () => {
                files.splice(index, 1);
                errorEl.textContent = '';
                syncInput();
                render();
            });

            grid.appendChild(col);
        });
    }

    input.addEventListener('change', function () {
        errorEl.textContent = '';

        Array.from(this.files).forEach(file => {
            if (!file.type.startsWith('image/')) {
                errorEl.textContent = `${file.name} is not an image.`;
            } else if (file.size > MAX_SIZE) {
                errorEl.textContent = `${file.name} is larger than 10 MB.`;
            } else if (files.length >= MAX_FILES) {
                errorEl.textContent = `You can upload up to ${MAX_FILES} photos.`;
            } else if (!files.some(f => sameFile(f, file))) {
                files.push(file);
            }
        });

        syncInput();
        render();
    });
})();