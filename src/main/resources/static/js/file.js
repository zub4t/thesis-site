let fileInput;
let uploadedFiles;

function setup() {
  noCanvas();

  fileInput = select("#file-input");
  uploadedFiles = select("#uploaded-files");

  fileInput.changed(handleFileUpload);
}

function handleFileUpload() {
  const files = fileInput.elt.files;
  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    console.log(file)
    const fileEmoji = createDiv(`📄${file.name}`);
    fileEmoji.class("file-emoji");
    fileEmoji.parent(uploadedFiles);
    fileEmoji.mouseClicked(() => {
      fileEmoji.remove();
    });
  }
}

