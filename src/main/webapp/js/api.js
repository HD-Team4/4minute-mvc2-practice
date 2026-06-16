document.addEventListener('DOMContentLoaded', () => {
    const pensionButtons = document.querySelectorAll('[data-pension-count]');

    loadWeather();
    loadPension(10);

    pensionButtons.forEach((button) => {
        button.addEventListener('click', () => {
            const count = Number(button.dataset.pensionCount || 10);
            setActivePensionCount(count);
            loadPension(count);
        });
    });
});

const KO = {
    loading: '불러오는 중...',
    currentTemp: '현재 기온',
    humidity: '습도',
    oneHourRain: '1시간 강수량',
    windSpeed: '풍속',
    baseTime: '기준 시각',
    noPension: '조회된 퇴직연금 상품 정보가 없습니다.',
    noRain: '강수 없음',
    rain: '비',
    rainSnow: '비/눈',
    snow: '눈',
    raindrop: '빗방울',
    raindropSnow: '빗방울눈날림',
    snowFlurry: '눈날림',
    checkRequired: '확인 필요',
    returnRate: '수익률',
    netAsset: '순자산',
    provider: '제공기관',
    fundCode: '펀드코드',
    empty: '-'
};

async function loadWeather() {
    const resultBox = document.querySelector('#weatherResult');
    if (!resultBox) return;

    resultBox.innerHTML = `<div class="text-muted">${KO.loading}</div>`;

    try {
        const data = await fetchJson('/api/weather');
        renderWeather(data);
    } catch (error) {
        resultBox.innerHTML = `<div class="text-danger">${escapeHtml(error.message)}</div>`;
    }
}

async function loadPension(count) {
    const resultBox = document.querySelector('#pensionResult');
    if (!resultBox) return;

    resultBox.innerHTML = `<div class="text-muted">${KO.loading}</div>`;

    try {
        const params = new URLSearchParams({ pageNo: '1', numOfRows: String(Math.max(count * 5, 50)) });
        const data = await fetchJson(`/api/pension?${params}`);
        renderPension(data, count);
    } catch (error) {
        resultBox.innerHTML = `<div class="text-danger">${escapeHtml(error.message)}</div>`;
    }
}

async function fetchJson(path) {
    const response = await fetch(`${getContextPath()}${path}`);
    const text = await response.text();
    let data;

    try {
        data = JSON.parse(text);
    } catch (error) {
        throw new Error(text || 'API response is not JSON.');
    }

    if (!response.ok || data.success === false) {
        throw new Error(data.message || 'API request failed.');
    }

    return data;
}

function renderWeather(data) {
    const resultBox = document.querySelector('#weatherResult');
    const items = normalizeItems(data?.response?.body?.items?.item);
    const values = {};
    items.forEach((item) => values[item.category] = item.obsrValue);

    const temp = getValue(values, 'T1H', '-');
    const humidity = getValue(values, 'REH', '-');
    const rainType = rainTypeName(values.PTY);
    const rainAmount = getValue(values, 'RN1', '0');
    const wind = getValue(values, 'WSD', '-');

    resultBox.innerHTML = `
        <div class="weather-current">
            <div>
                <span class="weather-label">${KO.currentTemp}</span>
                <strong>${escapeHtml(temp)}℃</strong>
            </div>
            <div class="weather-status">${escapeHtml(rainType)}</div>
        </div>
        <div class="row g-3 mt-1">
            <div class="col-6"><div class="metric-card"><span>${KO.humidity}</span><strong>${escapeHtml(humidity)}%</strong></div></div>
            <div class="col-6"><div class="metric-card"><span>${KO.oneHourRain}</span><strong>${escapeHtml(rainAmount)}mm</strong></div></div>
            <div class="col-6"><div class="metric-card"><span>${KO.windSpeed}</span><strong>${escapeHtml(wind)}m/s</strong></div></div>
            <div class="col-6"><div class="metric-card"><span>${KO.baseTime}</span><strong>${formatAccessTime()}</strong></div></div>
        </div>
    `;
}

function renderPension(data, requestedCount) {
    const resultBox = document.querySelector('#pensionResult');
    const body = data?.response?.body || data?.body || {};
    const items = normalizeItems(body?.items?.item || body?.items)
        .map(normalizePensionItem)
        .filter((item) => item.netAssetNumber > 0)
        .filter(uniquePensionItem)
        .slice(0, requestedCount);

    if (!items.length) {
        resultBox.innerHTML = `<div class="text-muted">${KO.noPension}</div>`;
        return;
    }

    const cards = items.map(renderPensionCard).join('');

    resultBox.innerHTML = `
        <div class="pension-diagram mb-3">${cards}</div>
    `;
}

function uniquePensionItem(item, index, list) {
    const key = item.fundCode !== KO.empty
        ? item.fundCode
        : `${item.operator}|${item.fundName}|${item.baseDate}`;
    return list.findIndex((candidate) => {
        const candidateKey = candidate.fundCode !== KO.empty
            ? candidate.fundCode
            : `${candidate.operator}|${candidate.fundName}|${candidate.baseDate}`;
        return candidateKey === key;
    }) === index;
}

function renderPensionCard(item) {
    return `
        <article class="pension-card">
            <div class="pension-card-topline">
                <span class="pension-provider">${escapeHtml(item.provider)}</span>
            </div>
            <h3>${escapeHtml(item.fundName)}</h3>
            <div class="pension-company">${escapeHtml(item.operator)}</div>
            <div class="pension-card-metrics">
                <div><span>${KO.returnRate}</span><strong>${escapeHtml(item.returnRate)}</strong></div>
                <div><span>${KO.netAsset}</span><strong>${escapeHtml(item.netAsset)}</strong></div>
            </div>
            <div class="pension-code">${KO.fundCode} ${escapeHtml(item.fundCode)}</div>
        </article>
    `;
}
function normalizePensionItem(item) {
    return {
        operator: firstValue(item, ['cmpyNm', 'ofrInstNm']),
        fundName: firstValue(item, ['fndNm']),
        baseDate: formatBaseDate(firstValue(item, ['basDt'])),
        returnRate: calculateReturnRate(firstValue(item, ['basprc'])),
        netAsset: formatAmount(firstValue(item, ['nPptAmt'])),
        netAssetNumber: toNumber(firstValue(item, ['nPptAmt'])),
        provider: firstValue(item, ['ofrInstNm']),
        fundCode: firstValue(item, ['fndCd'])
    };
}

function toNumber(value) {
    const number = Number(String(value ?? '').replaceAll(',', ''));
    return Number.isFinite(number) ? number : 0;
}
function formatBaseDate(value) {
    const text = String(value ?? '');
    if (/^\d{8}$/.test(text)) {
        return `${text.slice(0, 4)}.${text.slice(4, 6)}.${text.slice(6, 8)}`;
    }
    if (/^\d{6}$/.test(text)) {
        return `${text.slice(0, 4)}.${text.slice(4, 6)}`;
    }
    return text || KO.empty;
}

function calculateReturnRate(basePrice) {
    const initialBasePrice = 1000;
    const number = Number(String(basePrice ?? '').replaceAll(',', ''));
    if (!Number.isFinite(number)) return KO.empty;
    return `${(((number / initialBasePrice) - 1) * 100).toFixed(2)}%`;
}

function formatAmount(value) {
    if (value === KO.empty) return value;
    const number = Number(String(value).replaceAll(',', ''));
    if (!Number.isFinite(number)) return value;
    return number === 0 ? '0' : number.toLocaleString();
}

function firstValue(item, keys) {
    for (const key of keys) {
        const value = item?.[key];
        if (value !== undefined && value !== null && String(value).trim() !== '') {
            return value;
        }
    }
    return KO.empty;
}

function setActivePensionCount(count) {
    document.querySelectorAll('[data-pension-count]').forEach((button) => {
        const active = Number(button.dataset.pensionCount) === count;
        button.classList.toggle('active', active);
        button.classList.toggle('btn-primary', active);
        button.classList.toggle('btn-outline-primary', !active);
    });
}

function normalizeItems(value) {
    if (!value) return [];
    return Array.isArray(value) ? value : [value];
}

function getValue(values, key, fallback) {
    return values[key] === undefined || values[key] === null || values[key] === '' ? fallback : values[key];
}

function formatAccessTime() {
    const now = new Date();
    const yyyy = now.getFullYear();
    const mm = String(now.getMonth() + 1).padStart(2, '0');
    const dd = String(now.getDate()).padStart(2, '0');
    const hh = String(now.getHours()).padStart(2, '0');
    const mi = String(now.getMinutes()).padStart(2, '0');
    return `${yyyy}.${mm}.${dd} ${hh}:${mi}`;
}

function rainTypeName(value) {
    const map = {'0': KO.noRain, '1': KO.rain, '2': KO.rainSnow, '3': KO.snow, '5': KO.raindrop, '6': KO.raindropSnow, '7': KO.snowFlurry};
    return map[String(value ?? '0')] || KO.checkRequired;
}

function escapeHtml(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

function getContextPath() {
    if (window.CONTEXT_PATH !== undefined) return window.CONTEXT_PATH;
    const path = window.location.pathname;
    const index = path.indexOf('/', 1);
    return index === -1 ? '' : path.substring(0, index);
}
