<template>
  <div>
    <el-upload
      drag
      action="http://localhost:8080/file/upload"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      list-type="picture"
      :limit="1"
      :show-file-list="false"
      :file-list="fileList"
      :on-exceed="handleExceed"
      accept=".png, .jpg, .jpeg"
    >
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">拖拽文件到此处上传</div>
    </el-upload>
    <!-- 自定义图片和删除按钮 -->
    <div v-if="img" class="image-preview">
      <img :src="img" alt="头像url" style="width: 100px; height: 100px;" />
      <el-button
        type="danger"
        size="mini"
        icon="el-icon-delete"
        @click="handleRemoveImage"
        style="margin-left: 10px;"
      ></el-button>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'UploadImg',
    props: {
      pictureList: {
        type: Array,
        default: () => []
      },
      img: {
        type: String,
        default: ''
      }
    },
    data() {
      return {
        fileList: [] // 用局部变量管理文件列表
      };
    },
    methods: {
      handleUploadSuccess(response, file) {
        const imgUrl = "http://localhost:8080/file/download?id=" + response.data.id;
        // 通知父组件更新数据
        this.$emit('update:img', imgUrl);
        this.$emit('update:pictureList', [{ url: imgUrl }]);
        this.$message.success('文件上传成功');
      },
      handleUploadError(err, file) {
        this.$message.error('文件上传失败');
      },
      handleExceed() {
        this.$message.error('只能上传一个文件');
      },
      handleRemoveImage() {
        // 通知父组件清空数据
        this.$emit('update:img', '');
        this.$emit('update:pictureList', []);
      }
    },
    watch: {
      // 监听父组件传递的 pictureList，同步到本地 fileList
      pictureList: {
        immediate: true,
        handler(newVal) {
          this.fileList = newVal;
        }
      }
    }
  };
</script>
