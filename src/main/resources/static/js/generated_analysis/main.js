
var idsBssidsArray = [];
var expArray = [];
var myChart = null;
var EXPChoices = null;
var IDSChoices = null;
var regressionResult = null;
function onDocumentLoad() {

      EXPChoices = new Choices(document.getElementById('EXP'), {
        removeItemButton: true,

     });
     IDSChoices = new Choices(document.getElementById('IDS'), {
        removeItemButton: true,

     });
    EXPChoices.passedElement.element.addEventListener(
      'addItem',
      function(event) {
      if(event.detail.value=='all'){
            EXPChoices.config.choices.forEach(item=>{if(item.value!='all')EXPChoices.setValue([item])})
            EXPChoices.removeActiveItemsByValue('all');


      }else{
        expArray.push(event.detail.value)
      }
      },
      false,
    );
    IDSChoices.passedElement.element.addEventListener(
      'addItem',
      function(event) {
        if(event.detail.value=='all'){
            IDSChoices.config.choices.forEach(item=>{if(item.value!='all')IDSChoices.setValue([item])})
            IDSChoices.removeActiveItemsByValue('all');
        }else{
                idsBssidsArray.push(event.detail.value)

        }

      },
      false,
    );

     IDSChoices.passedElement.element.addEventListener(
          'removeItem',
          function(event) {
            idsBssidsArray = idsBssidsArray.filter(item => item !== event.detail.value);
          },
          false,
     );
     EXPChoices.passedElement.element.addEventListener(
          'removeItem',
          function(event) {
            expArray = expArray.filter(item => item !== event.detail.value);
          },
          false,
     );

  }

window.onload = onDocumentLoad;

function createChart() {
  if (myChart) {
    myChart.destroy(); // Destroy the existing chart
  }
  data_main = comparisonData.filter(item=>idsBssidsArray.includes(item.id)  && expArray.includes(item.exp) ).map(item => ({
                                    x: item.groundTruth,
                                    y: item.measurement,
                                    id: item.id
                                }));
    const start = 0;
    const end = 10;
    const step = 0.001;
    const range = [];

    for (let i = start; i <= end; i += step) {
      range.push(i);
    }

    regressionResult = regression.linear(data_main.map(data => [data.x, data.y]));
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
    const ctx = document.getElementById('comparisonChart').getContext('2d');
    myChart = new Chart(ctx, {
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
