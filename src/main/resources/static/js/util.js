var measurements = []

function gradientDescent(apPositions, apMeasurements) {
    // Define hyperparameters
    const learningRate = 0.01;
    const maxIterations = 1000;

    // Initialize position estimate
    let positionEstimate = [0, 0, 0];

    // Define helper functions
    function euclideanDistance(position, apPosition, apMeasurement) {
        return Math.sqrt(
            (position[0] - apPosition[0]) ** 2 +
            (position[1] - apPosition[1]) ** 2 +
            (position[2] - apPosition[2]) ** 2
        ) - apMeasurement;
    }

    function gradient(position) {
        let gradX = 0;
        let gradY = 0;
        let gradZ = 0;
        for (let i = 0; i < apPositions.length; i++) {
            const apPosition = apPositions[i];
            const apMeasurement = apMeasurements[i];
            const distance = euclideanDistance(position, apPosition, apMeasurement);
            gradX += distance * (position[0] - apPosition[0]);
            gradY += distance * (position[1] - apPosition[1]);
            gradZ += distance * (position[2] - apPosition[2]);
        }
        return [gradX, gradY, gradZ];
    }

    // Perform gradient descent
    for (let i = 0; i < maxIterations; i++) {
        const grad = gradient(positionEstimate);
        positionEstimate[0] -= learningRate * grad[0];
        positionEstimate[1] -= learningRate * grad[1];
        positionEstimate[2] -= learningRate * grad[2];
    }

    return positionEstimate;
}

async function getMeasurementsForExperiment(expNumber) {
    const response = await fetch(`http://localhost:8000/WIFI_CSV/EXP_${expNumber}/`);
    const files = await response.text();
    const measurements = {};
    const fileNames = files.trim().split('\n');
    const parser = new DOMParser();
    const doc = parser.parseFromString(fileNames, 'text/html');
    for (const fileName of doc.querySelectorAll("li")) {
        const ssid = fileName.innerText.slice(0, -4); // Remove the '.csv' extension
        const fileResponse = await fetch(`http://localhost:8000/WIFI_CSV/EXP_${expNumber}/${fileName.innerText}`);
        const data = await fileResponse.text();
        const rows = data.trim().split('\n').slice(1); // Skip the header row
        const ssidMeasurements = rows.map(row => {
            const [RSSI, xCoordinate, distanceStdDevM, BSSID, distance, yCoordinate, numAttemptedMeasurements, zCoordinate, time, rangingTimestampMillis, SSID, numSuccessfulMeasurements] = row.split(',');
            return { RSSI, xCoordinate, distanceStdDevM, BSSID, distance, yCoordinate, numAttemptedMeasurements, zCoordinate, time, rangingTimestampMillis, SSID, numSuccessfulMeasurements };
        });
        if (ssid in measurements) {
            measurements[ssid].push(...ssidMeasurements);
        } else {
            measurements[ssid] = ssidMeasurements;
        }
    }
    return measurements;
}

async function calculate() {
    const objectToRemove = scene.getObjectByName("smarphone");
    if (objectToRemove !== undefined) {
        scene.remove(objectToRemove);
    }
    measurements = await getMeasurementsForExperiment(23);
    console.log(measurements);
    var aux = []
    var aux1 = []
    for (let key of Object.keys(measurements)) {
        const min = 0;
        const max = measurements[key].length;
        const randomInt = Math.floor(Math.random() * (max - min + 1)) + min;
        let chosen = (measurements[key][randomInt])
        aux.push(chosen.distance)
        aux1.push([chosen.xCoordinate, chosen.yCoordinate, chosen.zCoordinate])

    }
    let c = gradientDescent(aux1, aux)
    c = c.map(x => x * 20)
    console.log(c)
    const coneGeometry = new THREE.ConeGeometry(1, 2, 32);

    // Create a blue material for the cone
    const coneMaterial = new THREE.MeshBasicMaterial({ color: 0x0000ff });

    // Create a mesh from the geometry and material, and position it
    const coneMesh = new THREE.Mesh(coneGeometry, coneMaterial);
    coneMesh.position.set(c[0], 0, c[1]);

    // Add the cone mesh to the scene and give it an ID for later reference
    coneMesh.name = "smarphone";
    scene.add(coneMesh);
}
document.addEventListener("keydown", function(event) {
    if (event.code === "Space") {
        calculate();
    }
});