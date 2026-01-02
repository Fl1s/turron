function wrapWordsPreservingLinks(element) {
    const walker = document.createTreeWalker(
        element,
        NodeFilter.SHOW_TEXT,
        {
            acceptNode(node) {
                return node.parentNode.tagName === 'A'
                    ? NodeFilter.FILTER_REJECT
                    : NodeFilter.FILTER_ACCEPT;
            }
        }
    );

    const textNodes = [];
    while (walker.nextNode()) {
        textNodes.push(walker.currentNode);
    }

    textNodes.forEach(node => {
        const words = node.textContent.split(/\s+/);
        const fragment = document.createDocumentFragment();

        words.forEach((word, i) => {
            const span = document.createElement('span');
            span.className = 'word';
            span.textContent = word;
            fragment.appendChild(span);
            if (i < words.length - 1) fragment.append(' ');
        });

        node.replaceWith(fragment);
    });
}

wrapWordsPreservingLinks(document.getElementById('extra'));
wrapWordsPreservingLinks(document.getElementById('about'));
wrapWordsPreservingLinks(document.getElementById('ans_1'));
wrapWordsPreservingLinks(document.getElementById('ans_2'));
wrapWordsPreservingLinks(document.getElementById('ans_3'));
wrapWordsPreservingLinks(document.getElementById('ans_4'));
wrapWordsPreservingLinks(document.getElementById('ans_5'));
wrapWordsPreservingLinks(document.getElementById('ans_6'));

const dropAreas = document.querySelectorAll('.drag-drop');

dropAreas.forEach(area => {
    area.addEventListener('dragover', (e) => {
        e.preventDefault();
        area.classList.add('dragover');
    });

    area.addEventListener('dragleave', () => {
        area.classList.remove('dragover');
    });

    area.addEventListener('drop', (e) => {
        e.preventDefault();
        area.classList.remove('dragover');
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            area.textContent = files[0].name;
        }
    });
});


async function loadLocale(lang = 'en') {
    try {
        const res = await fetch(`locales/${lang}.json`);
        const translations = await res.json();

        document.querySelectorAll('[i18n]').forEach(el => {
            const key = el.getAttribute('i18n');
            if (translations[key]) el.textContent = translations[key];
        });
    } catch (err) {
        console.error('Error loading translations:', err);
    }
}

const lang = navigator.language.startsWith('ru') ? 'ru' : 'en';
loadLocale(lang);

const API_GATEWAY = 'https://turron.pw';

async function uploadVideo(file, endpoint) {
    const formData = new FormData();
    formData.append('file', file);

    const res = await fetch(`${API_GATEWAY}${endpoint}`, {
        method: 'POST',
        body: formData
    });

    if (!res.ok) {
        throw new Error('[upload failed]');
    }

    return res.json();
}

dropAreas.forEach(area => {
    area.addEventListener('dragover', (e) => {
        e.preventDefault();
        area.classList.add('dragover');
    });

    area.addEventListener('dragleave', () => {
        area.classList.remove('dragover');
    });

    area.addEventListener('drop', async (e) => {
        e.preventDefault();
        area.classList.remove('dragover');

        const file = e.dataTransfer.files[0];
        await handleFileUpload(area, file);
    });

    area.addEventListener('click', () => {
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.accept = 'video/mp4';
        fileInput.style.display = 'none';
        document.body.appendChild(fileInput);

        fileInput.addEventListener('change', async () => {
            const file = fileInput.files[0];
            await handleFileUpload(area, file);
            fileInput.remove();
        });

        fileInput.click();
    });
});

async function handleFileUpload(area, file) {
    if (!file) return;

    if (file.type !== 'video/mp4') {
        alert('[only MP4 videos are supported!!! >=[ ]');
        return;
    }

    area.textContent = '[uploading...]';

    try {
        let result;

        if (area.id === 'upload_snippet') {
            result = await uploadVideo(file, '/api/v1/upload/snippet');
            area.textContent = '[snippet uploaded!]';
            document.getElementById('video_id').value = result.snippetId;
        }

        if (area.id === 'upload_source') {
            result = await uploadVideo(file, '/api/v1/upload/source');
            area.textContent = '[source uploaded]';
        }
    } catch (err) {
        console.error(err);
        area.textContent = '[upload failed...]';
    }
}


const loadButton = document.getElementById('load-video');
const videoEl = document.getElementById('video');
const downloadLink = document.getElementById('download');

loadButton.addEventListener('click', async () => {
    const snippetId = document.getElementById('video_id').value.trim();
    if (!snippetId) return;

    loadButton.textContent = 'Searching...';

    try {
        const res = await fetch(
            `${API_GATEWAY}/api/v1/search/best-match/${snippetId}`
        );

        if (!res.ok) {
            throw new Error('[search failed...]');
        }

        const {downloadUrl} = await res.json();

        videoEl.src = downloadUrl;
        videoEl.load();

        downloadLink.href = downloadUrl;

    } catch (err) {
        console.error(err);
        alert('[source not found... the snippets database may have been cleared, please try again!]');
    } finally {
        loadButton.textContent = 'Enter';
    }
});
