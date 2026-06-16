document.addEventListener('DOMContentLoaded', () => {
    loadKakaoMap();
});

const COMPANY_LAT = 37.4948802;
const COMPANY_LNG = 127.1222790;

function showMapFallback() {
    document.querySelector('#map')?.classList.add('d-none');
    document.querySelector('#mapFallback')?.classList.remove('d-none');
}

function loadKakaoMap() {
    const container = document.querySelector('#map');
    if (!container) return;
    const appKey = container.dataset.appKey;
    if (!appKey) {
        showMapFallback();
        return;
    }

    const script = document.createElement('script');
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${encodeURIComponent(appKey)}&autoload=false`;
    script.onload = () => {
        kakao.maps.load(() => {
            const position = new kakao.maps.LatLng(COMPANY_LAT, COMPANY_LNG);
            const map = new kakao.maps.Map(container, {center: position, level: 3});
            new kakao.maps.Marker({map, position});
        });
    };
    script.onerror = showMapFallback;
    document.head.appendChild(script);
}
