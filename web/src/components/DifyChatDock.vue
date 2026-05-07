<template>
  <div class="dify-chat-dock">
    <transition name="fade">
      <div v-show="panelOpen" class="dock-panel">
        <div class="dock-header">
          <span class="dock-title">birdme</span>
          <button type="button" class="dock-icon-btn" title="Close" @click="panelOpen = false">×</button>
        </div>
        <div ref="scrollRef" class="dock-messages">
          <div v-if="!messages.length" class="dock-empty">
            Hi, I am BirdMe Assistant. I can help you with navigation, bird rescue, and bird knowledge Q&amp;A.<br>
            Try asking:<br>
            1. Navigation: "I want to go to the knowledge page."<br>
            2. Rescue: "I found an injured bird and I want to help."<br>
            3. Q&amp;A: "Can you tell me what the common bird species in China are?"
          </div>
          <div
            v-for="(m, idx) in messages"
            :key="idx"
            class="dock-row"
            :class="m.role === 'user' ? 'is-user' : 'is-assistant'"
          >
            <div class="dock-bubble">
              <div v-if="m.images && m.images.length" class="dock-msg-images">
                <img v-for="(src, ii) in m.images" :key="ii" :src="src" alt="" />
              </div>
              <span v-if="m.text">{{ m.text }}</span>
            </div>
          </div>

          <el-dialog :visible.sync="rescueDialogVisible" title="Submit Rescue Request" width="600px" @close="resetRescueForm"
                     @focusout="resetRescueForm" append-to-body>
            <el-form :model="rescueForm" label-width="140px" :rules="rescueFormRules" ref="rescueFormRef">
              <el-form-item label="name" prop="name">
                <el-select v-model="rescueForm.name"
                           placeholder="Select rescue station"
                           style="width: 100%;"
                           :popper-append-to-body="false"
                           @change="selectStation"
                           clearable>
                  <el-option v-for="item in displayStations" :label="item.name" :value="item.name" :key="item.id"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="Location" prop="location">
                <el-input v-model="rescueForm.location" disabled></el-input>
              </el-form-item>
              <el-form-item label="Latitude" prop="latitude">
                <el-input v-model="rescueForm.latitude" disabled></el-input>
              </el-form-item>
              <el-form-item label="Longitude" prop="longitude">
                <el-input v-model="rescueForm.longitude" disabled></el-input>
              </el-form-item>
              <el-form-item label="Bird Name" prop="birdName">
                <el-input v-model="rescueForm.birdName" placeholder="Enter bird name"></el-input>
              </el-form-item>
              <el-form-item label="Species" prop="species">
                <el-input v-model="rescueForm.species" placeholder="Enter species (optional)"></el-input>
              </el-form-item>
              <el-form-item label="Injury Type" prop="injuryType">
                <el-select v-model="rescueForm.injuryType" placeholder="Select injury type" style="width: 100%;">
                  <el-option label="Unable to Fly" value="Unable to Fly"></el-option>
                  <el-option label="Wing Injury" value="Wing Injury"></el-option>
                  <el-option label="Leg Injury" value="Leg Injury"></el-option>
                  <el-option label="Eye Injury" value="Eye Injury"></el-option>
                  <el-option label="Poisoning" value="Poisoning"></el-option>
                  <el-option label="Exhaustion" value="Exhaustion"></el-option>
                  <el-option label="Other" value="Other"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="Description" prop="injuryDescription">
                <el-input type="textarea" v-model="rescueForm.injuryDescription" rows="4" placeholder="Describe the bird's condition"></el-input>
              </el-form-item>
              <el-form-item label="Image" prop="imageUrl">
                <UploadImg
                  ref="uploadImg"
                  buttonText="Upload image"
                  @image-uploaded="handleAvatarUploaded"
                />
              </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
        <el-button @click="rescueDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitRescueForm" :loading="submitLoading">Submit</el-button>
      </span>
          </el-dialog>
        </div>
        <div class="dock-compose">
          <div v-if="pendingFiles.length" class="dock-pending-images">
            <div v-for="(p, pi) in pendingFiles" :key="p.key" class="dock-pending-item">
              <img :src="p.preview" alt="" />
              <button type="button" class="dock-remove-img" title="Remove" @click="removePending(pi)">×</button>
            </div>
          </div>
          <div class="dock-input-row">
            <input
              ref="imageInput"
              type="file"
              class="dock-file-input"
              accept="image/*"
              multiple
              @change="onPickImages"
            >
            <el-button type="default" class="dock-image-btn" title="Add image" @click="$refs.imageInput.click()">
              <i class="el-icon-picture-outline" />
            </el-button>
            <el-input
              v-model="draft"
              type="textarea"
              :rows="2"
              placeholder="Talk to birdme (optional if you send an image)"
              resize="none"
              class="dock-text-input"
              @keyup.enter.native.exact.prevent="send"
            />
            <el-button type="primary" class="dock-send" :loading="loading" @click="send">Send</el-button>
          </div>
        </div>
      </div>
    </transition>
    <button
      type="button"
      class="dock-fab"
      aria-label="Open chat"
      @click="panelOpen = !panelOpen"
    >
      <span v-if="!panelOpen">💬</span>
      <span v-else>▼</span>
    </button>
  </div>
</template>

<script>
const MAX_CHAT_IMAGES = 4
import { sendDifyChatAndNavigate, uploadDifyFile } from '@/api/dify'
import UploadImg from '@/components/UploadImg/index.vue'
import { insertReport } from '@/api/report'
import { getNearbyStations, allRescueStation } from '@/api/rescueStation'
import { reverseGeocode, searchNearbyPetHospitals } from '@/api/help'

export default {
  name: 'DifyChatDock',
  components: {
    UploadImg
  },
  data() {
    return {
      panelOpen: false,
      draft: '',
      loading: false,
      conversationId: '',
      messages: [],
      pendingFiles: [],
      rescueForm: {
        birdName: '',
        species: '',
        name: '',
        location: '',
        latitude: '',
        longitude: '',
        injuryType: '',
        injuryDescription: '',
        imageUrl: '',
        submitterId: '',
        rescueStationId: ''
      },
      userLocation: {
        latitude: null,
        longitude: null,
        location: null
      },
      rescueDialogVisible: false,
      submitLoading: false,
      rescueFormRules: {
        birdName: [{ required: true, message: 'Please enter bird name', trigger: 'blur' }],
        injuryType: [{ required: true, message: 'Please select injury type', trigger: 'change' }],
        injuryDescription: [{ required: true, message: 'Please enter description', trigger: 'blur' }]
      },
      nearbyStations: [],
      displayStations: [],
    }
  },
  beforeDestroy() {
    this.revokeAllPending()
  },
  methods: {
    onPickImages(e) {
      const input = e.target
      const picked = Array.from(input.files || [])
      input.value = ''
      for (const file of picked) {
        if (!file.type || !file.type.startsWith('image/')) {
          this.$message.warning('Only image files are allowed')
          continue
        }
        if (this.pendingFiles.length >= MAX_CHAT_IMAGES) {
          this.$message.warning(`You can attach up to ${MAX_CHAT_IMAGES} images`)
          break
        }
        this.pendingFiles.push({
          key: `${Date.now()}-${Math.random()}`,
          file,
          preview: URL.createObjectURL(file)
        })
      }
    },
    removePending(index) {
      const p = this.pendingFiles[index]
      if (p && p.preview) {
        URL.revokeObjectURL(p.preview)
      }
      this.pendingFiles.splice(index, 1)
    },
    revokeAllPending() {
      this.pendingFiles.forEach(p => {
        if (p.preview) URL.revokeObjectURL(p.preview)
      })
      this.pendingFiles = []
    },
    async send() {
      const query = (this.draft || '').trim()
      const hasPending = this.pendingFiles.length > 0
      if ((!query && !hasPending) || this.loading) return

      this.loading = true

      try {
        let files
        const previews = this.pendingFiles.map(p => p.preview)
        if (hasPending) {
          const ids = []
          for (const pf of this.pendingFiles) {
            const up = await uploadDifyFile(pf.file)
            if (up.code !== 200 || !up.data || !up.data.uploadFileId) {
              this.$message.error(up.msg || 'Image upload failed')
              return
            }
            ids.push(up.data.uploadFileId)
          }
          files = ids.map(upload_file_id => ({
            type: 'image',
            transfer_method: 'local_file',
            upload_file_id
          }))
        }

        const displayText = query || '(Image)'
        this.messages.push({
          role: 'user',
          text: displayText,
          images: hasPending ? previews.slice() : undefined
        })
        if (hasPending) {
          this.revokeAllPending()
        }
        this.draft = ''
        this.$nextTick(this.scrollToBottom)

        const effectiveQuery = query || 'Describe this image.'
        const res = await sendDifyChatAndNavigate(
          {
            query: effectiveQuery,
            conversationId: this.conversationId || undefined,
            ...(files ? { files } : {})
          },
          this.$router
        )

        if (res.code === 200 && res.data) {
          if (res.data.conversationId) {
            this.conversationId = res.data.conversationId
          }
          const answer = typeof res.data.answer === 'string' ? res.data.answer : ''

          if("help process" === answer){
            this.messages.push({ role: 'assistant', text: 'Currently processing, please wait.' })
            this.getCurrentLocation()
            return
          }

          this.messages.push({ role: 'assistant', text: answer || '(empty reply)' })
        } else if (res && res.msg) {
          this.$message.error(res.msg)
        }
      } catch (e) {
        if (!e || e.message !== 'Unauthorized') {
          this.$message.error(e && e.message ? e.message : 'Request failed')
        }
      } finally {
        this.loading = false
        this.$nextTick(this.scrollToBottom)
      }
    },
    scrollToBottom() {
      const el = this.$refs.scrollRef
      if (el) {
        el.scrollTop = el.scrollHeight
      }
    },
    getCurrentLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            this.userLocation.latitude = position.coords.latitude
            this.userLocation.longitude = position.coords.longitude

            try {
              reverseGeocode(
                this.userLocation.latitude,
                this.userLocation.longitude
              ).then(res => {
                if (res.code === 200) {
                  this.userLocation.location = res.data;

                  // Check nearby pet hospitals
                  this.checkAndAddNearbyPetHospitals()
                }
              })
            } catch (error) {
              console.error('Get location error:', error);
            }
          },
          (error) => {
            console.error('Error getting location:', error)
            this.$message.error('Failed to get your location. Please search for a location manually.')
          }
        )
      } else {
        this.$message.error('Geolocation is not supported by your browser.')
      }
    },
    checkAndAddNearbyPetHospitals() {
      const radius = 30000
      if (this.userLocation.latitude && this.userLocation.longitude) {
          allRescueStation()
            .then(response => {
              if (response.code === 200 && response.data) {
                this.nearbyStations = response.data
                console.log(this.nearbyStations)

                const stationsWithDistance = this.nearbyStations.map(station => {
                  const distance = this.calculateDistance(
                    this.userLocation.latitude,
                    this.userLocation.longitude,
                    station.latitude,
                    station.longitude
                  )
                  return {
                    ...station,
                    distance: distance,
                    distanceKm: (distance / 1000).toFixed(2)
                  }
                })

                if (stationsWithDistance.length < 3) {
                  this.searchAndAddPetHospitals(stationsWithDistance)
                }else {
                  const sortedStations = stationsWithDistance.sort((a, b) => a.distance - b.distance)
                  this.displayStations = sortedStations.slice(0, 3);
                  this.rescueDialogVisible = true
                  this.messages.push({ role: 'assistant', text: 'Please complete the bird injury report.' })
                }
              }
            })
            .catch(error => {
              console.error('Error loading all stations:', error)
            })
      }
    },
    calculateDistance(lat1, lon1, lat2, lon2) {
      const R = 6371000
      const dLat = this.deg2rad(lat2 - lat1)
      const dLon = this.deg2rad(lon2 - lon1)
      const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(this.deg2rad(lat1)) * Math.cos(this.deg2rad(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2)
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
      return R * c
    },
    deg2rad(deg) {
      return deg * (Math.PI / 180)
    },
    searchAndAddPetHospitals(stationsWithDistance) {
      searchNearbyPetHospitals(this.userLocation.latitude, this.userLocation.longitude, 30000)
        .then(response => {
          if (response.code === 200 && response.data) {
            stationsWithDistance = [...stationsWithDistance, ...response.data]
          } else {
            console.log('No pet hospitals found or error:', response)
          }

          const sortedStations = stationsWithDistance.sort((a, b) => a.distance - b.distance)
          this.displayStations = sortedStations.slice(0, 3);
          this.rescueDialogVisible = true
          this.messages.push({ role: 'assistant', text: 'Please complete the bird injury report.' })
        })
        .catch(error => {
          console.error('Error searching pet hospitals:', error)
        })
    },
    selectStation(){
      for (const station of this.nearbyStations) {
        if (station.name === this.rescueForm.name) {
          this.rescueForm.rescueStationId = station.id
          this.rescueForm.location = station.address
          this.rescueForm.latitude = station.latitude
          this.rescueForm.longitude = station.longitude
          break
        }
      }
    },
    handleAvatarUploaded(data) {
      const { file } = data;
      const formData = new FormData();
      formData.append('file', file);

      fetch('http://localhost:8080/file/upload', {
        method: 'POST',
        body: formData,
      })
        .then(response => response.json())
        .then(data => {
          if(data.code === 200){
            this.rescueForm.imageUrl = 'http://localhost:8080/file/download?id=' + data.data.id;
            this.$message.success('Image uploaded successfully');
          }else{
            this.$message.error(data.msg);
          }
        })
        .catch(error => {
          this.$message.error('Image upload failed');
        });
    },
    async submitRescueForm() {
      if (!this.rescueForm.birdName || !this.rescueForm.injuryType || !this.rescueForm.injuryDescription) {
        this.$message.error('Please fill all required fields')
        return
      }

      this.submitLoading = true
      try {
        let userId = parseInt(sessionStorage.getItem('id'))

        const reportData = {
          birdName: this.rescueForm.birdName,
          species: this.rescueForm.species || 'Unknown',
          name: this.rescueForm.name,
          location: this.rescueForm.location,
          latitude: this.rescueForm.latitude,
          longitude: this.rescueForm.longitude,
          injuryType: this.rescueForm.injuryType,
          injuryDescription: this.rescueForm.injuryDescription,
          imageUrl: this.rescueForm.imageUrl,
          submitterId: userId ? userId.toString() : null,
          userLocation: this.userLocation.location,
          userLatitude: this.userLocation.latitude,
          userLongitude: this.userLocation.longitude,
          rescueStationId: this.rescueForm.rescueStationId
        }

        insertReport(reportData).then(response => {
          if (response.code === 200) {
            this.messages.push({ role: 'assistant', text: 'Submitted successfully. Please view your report later.' })
            this.rescueDialogVisible = false
            this.resetRescueForm()
          } else {
            this.$message.error(response.msg || 'Failed to submit rescue report')
          }
        })
      } catch (error) {
        console.error('Error submitting rescue report:', error)
        this.$message.error('Failed to submit rescue report')
      } finally {
        this.submitLoading = false
      }
    },
    resetRescueForm() {
      this.rescueForm = {
        name: '',
        location: '',
        latitude: '',
        longitude: '',
        birdName: '',
        species: '',
        injuryType: '',
        injuryDescription: '',
        imageUrl: ''
      }
      if (this.$refs.rescueFormRef) {
        this.$refs.rescueFormRef.resetFields()
      }
      this.rescueDialogVisible = false

      this.messages.push({ role: 'assistant', text: 'The user has canceled the rescue.' })
    },
  }
}
</script>

<style scoped lang="scss">
.dify-chat-dock {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 9998;
  font-family: system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif;
}

.dock-fab {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: none;
  background:rgb(242, 28, 231);
  color: #fff;
  font-size: 22px;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(116, 27, 117, 0.91);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 10px 28px rgba(116, 27, 117, 0.91);
  }
}

.dock-panel {
  position: absolute;
  right: 0;
  bottom: 72px;
  width: 24rem;
  max-width: calc(100vw - 40px);
  height: 40rem;
  max-height: calc(100vh - 120px);
  background: #f3f6fb;
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.dock-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: linear-gradient(90deg, rgb(28, 235, 242), rgba(246, 59, 212, 0.44));
  color: #fff;
  font-weight: 600;
}

.dock-icon-btn {
  border: none;
  background: transparent;
  color: #fff;
  font-size: 22px;
  line-height: 1;
  cursor: pointer;
  padding: 0 4px;
  opacity: 0.9;

  &:hover {
    opacity: 1;
  }
}

.dock-messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.dock-empty {
  font-size: 13px;
  color: #64748b;
  padding: 8px;
  line-height: 1.5;
}

.dock-row {
  display: flex;
  margin-bottom: 10px;

  &.is-user {
    justify-content: flex-end;
  }

  &.is-assistant {
    justify-content: flex-start;
  }
}

.dock-bubble {
  max-width: 85%;
  padding: 10px 12px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.45;
  white-space: pre-wrap;
  word-break: break-word;
}

.is-user .dock-bubble {
  background: #1ce0f2;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.is-assistant .dock-bubble {
  background: #fff;
  color: #0f172a;
  border: 1px solid #e2e8f0;
  border-bottom-left-radius: 4px;
}

.dock-compose {
  background: #fff;
  border-top: 1px solid #e2e8f0;
}

.dock-pending-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 10px 0;
}

.dock-pending-item {
  position: relative;
  width: 72px;
  height: 72px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
}

.dock-remove-img {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 22px;
  height: 22px;
  border: none;
  border-radius: 50%;
  background: rgba(15, 23, 42, 0.65);
  color: #fff;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  padding: 0;
}

.dock-msg-images {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 6px;

  img {
    max-width: 160px;
    max-height: 120px;
    border-radius: 8px;
    object-fit: cover;
    vertical-align: middle;
    border: 1px solid rgba(255, 255, 255, 0.35);
  }
}

.is-assistant .dock-msg-images img {
  border-color: #e2e8f0;
}

.dock-input-row {
  display: flex;
  gap: 8px;
  align-items: flex-end;
  padding: 10px;
}

.dock-file-input {
  position: absolute;
  width: 0;
  height: 0;
  opacity: 0;
  pointer-events: none;
}

.dock-image-btn {
  flex-shrink: 0;
  align-self: stretch;
  min-height: 54px;
  padding: 0 12px;
  font-size: 18px;
}

.dock-text-input {
  flex: 1;
  min-width: 0;
}

.dock-input-row ::v-deep .el-textarea__inner {
  font-size: 14px;
}

.dock-send {
  flex-shrink: 0;
  align-self: stretch;
  height: auto;
  min-height: 54px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

.el-select-dropdown {
  z-index: 9999 !important;
}

@media (max-width: 480px) {
  .dock-panel {
    width: calc(100vw - 32px);
    right: -8px;
    height: 70vh;
    max-height: 70vh;
  }
}
</style>
