
const collections = {
    LocalAuthority: {
        limit: 1000,
        style: { fillOpacity: 0, color: '#c0c', },
    },
    WaterBody: {
    },
    BiosysSite: {
        limit: 200,
    },
    Embankment: {
        style: { color: '#0a0', weight: 5, },
    },
}
for (let id in collections) collections[id].id = id;

const map = L.map('map').setView([55, -2], 6);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, ' +
    'Contains OS data &copy; Crown copyright and database right 2022.'
}).addTo(map);

// when moving around a lot, cancel in-progress requests
var resetFetch = new AbortController();

async function loadCollection(coll, opts) {
    const sw = opts.bounds.getSouthWest();
    const ne = opts.bounds.getNorthEast();
    const bbox = `${sw.lat},${sw.lng},${ne.lat},${ne.lng}`;
    const query = `bbox=${bbox}&limit=${opts.limit || 100}`;
    return await fetch(`/collections/${coll}/items?${query}`, {
            signal: resetFetch.signal
        }).then(response => response.json());
}

// simple button to display related locations
async function showLocation(uri) {
    const data = await fetch(uri).then(response => response.json());
    const layer = L.geoJSON(data, {
        style: () => ({color: '#f66'}),
    });
    layer.addTo(map); // (!) not cleaned up
    map.flyToBounds(layer.getBounds());
}
function linkHtml(link) {
    if (link.type == 'application/geo+json'
        && !(['self', 'collection'].includes(link.rel))) {
        return `<input type=button
            onclick='showLocation("${link.href}")'
            value="Show ${link.rel}" />`;
    }
}

// in order to view only the visible features, remove/readd layers on pan/zoom
// this keeps track of layers to remove next reload
var dynLayers = [];

async function reload() {
    // (!) cancelling too rapidly will make the db sad and return 500 with html
    // resetFetch.abort();
    // resetFetch = new AbortController();
    var loaders = {};
    for (let c of Object.values(collections)) {
        // TODO figure out how to keep hidden on update
        loaders[c.id] = loadCollection(c.id, {
            limit: c.limit,
            bounds: map.getBounds(),
        });
    }
    var layers = {};
    for (let l in loaders) {
        const data = await loaders[l];
        layers[l] = L.geoJSON(data, {
            style: () => collections[l].style,
            onEachFeature: (f, layer) => {
                var popup = `<p><b>${f.properties.title}</b></p>`;
                for (let link of f.links.map(linkHtml)) {
                    if (link) {
                        popup += `<p>${link}</p>`;
                    }
                }
                layer.bindPopup(popup);
            },
        });
    }

    // update all layers at once
    var newLayers = Object.values(layers);
    newLayers.push(L.control.layers(null, layers));
    for (let layer of newLayers) layer.addTo(map);
    for (let layer of dynLayers) layer.remove();
    dynLayers = newLayers;
};

reload();
map.on('zoomend moveend', reload);

