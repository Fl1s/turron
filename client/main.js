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
        if(files.length > 0){
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
