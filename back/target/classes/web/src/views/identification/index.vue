<template>
  <div class="identification-page">
    <div class="container">
      <!-- Page Title -->
      <div class="section-title">
        <h1>Bird Identification</h1>
        <p>Upload bird images to get detailed information</p>
      </div>
      
      <!-- Upload Area -->
      <div class="upload-section">
        <div class="upload-card" 
             :class="{ 'dragover': isDragover }"
             @dragover.prevent="onDragOver"
             @dragleave.prevent="onDragLeave"
             @drop.prevent="onDrop">
          <div class="upload-content">
            <div class="upload-icon">
              <i class="fas fa-cloud-upload-alt"></i>
            </div>
            <h3>Upload Bird Image</h3>
            <p>Click or drag image here</p>
            <input type="file" 
                   class="file-input" 
                   @change="handleImageUpload" 
                   accept="image/*">
          </div>
        </div>
      </div>

      <!-- Image Display -->
      <div v-if="imageSrc" class="image-section">
        <div class="image-card">
          <img :src="imageSrc" alt="Uploaded Image">
        </div>
      </div>

      <!-- Identification Result -->
      <div v-if="form.name" class="result-section">
        <div class="result-card">
          <div class="result-header">
            <h2 class="bird-name">{{ form.name }}</h2>
          </div>
          <div class="result-body">
            <p class="bird-description">{{ form.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      imageSrc: '',
      form: {},
      isDragover: false
    }
  },
  methods: {
    handleImageUpload(event) {
      const file = event.target.files[0];
      this.processFile(file);
    },
    onDragOver() {
      this.isDragover = true;
    },
    onDragLeave() {
      this.isDragover = false;
    },
    onDrop(event) {
      this.isDragover = false;
      const file = event.dataTransfer.files[0];
      if (file && file.type.startsWith('image/')) {
        this.processFile(file);
      } else {
        this.$message.error("Please upload image file");
      }
    },
    processFile(file) {
      if (file) {
        const reader = new FileReader();
        reader.onloadend = () => {
          this.imageSrc = reader.result;
        };
        reader.readAsDataURL(file);

        const formData = new FormData();
        formData.append('file', file); 
        fetch('http://localhost:8080/file/identification', {
          method: 'POST',
          body: formData,
        })
        .then(response => response.json())
        .then(data => {
          if(data.code === 200){
            this.form = data.data
          }else{
            this.$message.error(data.msg);
          }
        })
        .catch(error => {
          this.$message.error("Identification failed");
        });
      }else{
        this.$message.error("Please upload image");
      }
    }
  }
}
</script>

<style scoped>
.identification-page {
  padding: 60px 0;
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  margin-bottom: 50px;
}

.section-title h1 {
  font-size: 36px;
  color: #22b3c1;
  margin-bottom: 15px;
  font-weight: 700;
}

.section-title p {
  font-size: 18px;
  color: #666;
  margin: 0;
}

.upload-section {
  margin-bottom: 40px;
}

.upload-card {
  background: white;
  border-radius: 12px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 2px dashed #b2ebf2;
  cursor: pointer;
}

.upload-card:hover {
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
  border-color: #4dd0e1;
}

.upload-card.dragover {
  border-color: #22b3c1;
  background-color: #e0f7fa;
}

.upload-icon {
  font-size: 60px;
  color: #4dd0e1;
  margin-bottom: 20px;
}

.upload-content h3 {
  font-size: 24px;
  color: #22b3c1;
  margin-bottom: 10px;
  font-weight: 600;
}

.upload-content p {
  color: #666;
  margin-bottom: 30px;
  font-size: 16px;
}

.file-input {
  display: inline-block;
  background: linear-gradient(135deg, #4dd0e1 0%, #22b3c1 100%);
  color: white;
  padding: 12px 30px;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  outline: none;
}

.file-input:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(77, 208, 225, 0.4);
}

.image-section {
  margin-bottom: 40px;
}

.image-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  text-align: center;
  overflow: hidden;
}

.image-card img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.image-card:hover img {
  transform: scale(1.02);
}

.result-section {
  margin-bottom: 40px;
}

.result-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.result-card:hover {
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.result-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e0f7fa;
}

.bird-name {
  font-size: 28px;
  color: #22b3c1;
  margin: 0;
  font-weight: 700;
}

.bird-description {
  color: #666;
  line-height: 1.8;
  font-size: 16px;
  margin: 0;
}

/* Responsive Design */
@media (max-width: 768px) {
  .identification-page {
    padding: 40px 0;
  }
  
  .section-title h1 {
    font-size: 28px;
  }
  
  .upload-card {
    padding: 40px 20px;
  }
  
  .upload-icon {
    font-size: 40px;
  }
  
  .upload-content h3 {
    font-size: 20px;
  }
  
  .result-card {
    padding: 20px;
  }
  
  .bird-name {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .section-title h1 {
    font-size: 24px;
  }
  
  .upload-content h3 {
    font-size: 18px;
  }
  
  .file-input {
    padding: 10px 20px;
    font-size: 14px;
  }
}
</style>