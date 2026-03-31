<template>
  <div class="upload-section">
    <div class="upload-card"
         :class="{ 'dragover': isDragover }"
         @dragover.prevent="onDragOver"
         @dragleave.prevent="onDragLeave"
         @drop.prevent="onDrop">
      <input ref="fileInput"
             type="file"
             class="file-input"
             @change="handleImageUpload"
             :accept="accept || 'image/*'">
      <div v-if="imageSrc" class="preview-image">
        <img :src="imageSrc" alt="Uploaded Image">
      </div>
      <div v-else class="upload-content">
        <el-button type="primary" class="upload-button">
          {{ buttonText || 'Upload Image' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UploadImg',
  props: {
    buttonText: {
      type: String,
      default: 'Upload Image'
    },
    accept: {
      type: String,
      default: 'image/*'
    }
  },
  data() {
    return {
      imageSrc: '',
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
          this.$emit('image-uploaded', { file, imageSrc: this.imageSrc });
        };
        reader.readAsDataURL(file);
      } else {
        this.$message.error("Please upload image");
      }
    },
    reset() {
      this.imageSrc = '';
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = '';
      }
    }
  }
}
</script>

<style scoped>
.upload-section {
  margin-bottom: 30px;
}

.upload-section h4 {
  color: #333;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}

.upload-card {
  border: 2px dashed #b2ebf2;
  border-radius: 8px;
  padding: 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8f9fa;
  position: relative;
  overflow: hidden;
}

.upload-card:hover {
  border-color: #4dd0e1;
  background-color: #e0f7fa;
}

.upload-card.dragover {
  border-color: #4dd0e1;
  background-color: rgba(77, 208, 225, 0.1);
}

.file-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 10;
}

.preview-image {
  margin: 0 auto;
  width: 300px;
  height: 300px;
  overflow: hidden;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
}

.preview-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
}

.upload-button {
  background: linear-gradient(135deg, #4dd0e1 0%, #22b3c1 100%);
  border: none;
  padding: 12px 30px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 50px;
  transition: all 0.3s ease;
}

.upload-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(77, 208, 225, 0.4);
  background: linear-gradient(135deg, #22b3c1 0%, #0097a7 100%);
}

@media (max-width: 768px) {
  .upload-card {
    padding: 20px;
  }

  .upload-button {
    padding: 10px 24px;
    font-size: 13px;
  }

  .preview-image {
    max-height: 150px;
  }
}
</style>
