

function onDocumentLoad() {
    onlyTAG = comparisonData.filter(item=>item.id.length == 4).map(item => ({
                                      x: item.groundTruth,
                                      y: item.measurement,
                                      id: item.id
                                  }));
    only802 = comparisonData.filter(item=>item.id.length > 4).map(item => ({
                              x: item.groundTruth,
                              y: item.measurement,
                              id: item.id
                          }))
    all = comparisonData.map(item => ({
                                x: item.groundTruth,
                                y: item.measurement,
                                id: item.id
                            }))
    createChart('comparisonOnlyTag',onlyTAG)
    createChart('comparisonOnly802',only802)
    createChart('comparisonChart',all)

  }

  window.onload = onDocumentLoad;

function createChart(id,data_main) {
    const start = 0;
    const end = 10;
    const step = 0.001;
    const range = [];

    for (let i = start; i <= end; i += step) {
      range.push(i);
    }

    const regressionResult = regression.linear(data_main.map(data => [data.x, data.y]));
    const lmsLine = {
        type: 'line',
        data: {
            datasets: [
                {
                    label: 'LMS Line',
                    data: range.map(data => ({
                        x: data,
                        y: regressionResult.predict(data)[1]
                    })),
                    borderColor: 'rgba(255, 0, 0, 1)',
                    backgroundColor: 'rgba(0, 0, 0, 0)',
                    borderWidth: 1,
                    fill: false
                }
            ]
        }
    };
    const ctx = document.getElementById(id).getContext('2d');
    let chart = new Chart(ctx, {
        type: 'scatter',
        data: {
            datasets: [
              {
                        label: 'y=x line',
                        data: [{
                                           x: 0,
                                           y: 0
                                         },
                                         {
                                           x: 10,
                                           y: 10
                                         }
                                         ],
                        borderColor:  'rgba(255, 45, 0, 1)',
                        type: 'line', // Set the type for this dataset to 'line'
                        fill: false,
            },
            {
                        label: 'LMS',
                        data: [{
                          x: 0,
                          y: regressionResult.predict(0)[1]
                        },
                        {
                          x: 10,
                          y: regressionResult.predict(10)[1]
                        }
                        ],
                        borderColor:  'rgba(255, 255, 0, 1)',
                        type: 'line', // Set the type for this dataset to 'line'
                        fill: false,
            },
            { label: 'groundTruth X measurement',

                data: data_main,
                pointBackgroundColor: data_main.map(item => item.id.length === 4 ? 'rgba(75, 192, 192, 1)' : 'rgba(54, 162, 235, 1)'),
                pointBorderColor: data_main.map(item => item.id.length === 4 ? 'rgba(75, 192, 192, 1)' : 'rgba(54, 162, 235, 1)'),
                borderWidth: 1
            }
            ]
        },
        options: {
             events: [], // Disable all mouse interactions
             plugins: {
                      tooltip: {
                          enabled: false
                      }
                  },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Ground Truth'
                    }
                },
                y: {
                    title: {
                        display: true,
                        text: 'Measurement'
                    }
                }
            }
        }
    });
}
