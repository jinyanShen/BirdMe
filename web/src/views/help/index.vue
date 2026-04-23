<template>
  <div class="help-container">
    <div class="help-header">
      <h1>Help Center</h1>
      <p>Get assistance with bird rescue and reporting</p>
    </div>
    <div class="map-section">
      <div class="map-controls">
        <div class="search-box">
          <el-input
            v-model="searchQuery"
            placeholder="Search for location"
            prefix-icon="el-icon-search"
            clearable
            @keyup.enter.native="handleSearch"
            @focus="showLists = true"
            @blur="handleSearchBlur"
          >
            <el-button slot="append" @click="toggleLists">Lists</el-button>
          </el-input>
          <div v-if="searchResults.length > 0 && showLists" class="search-results">
          <div v-for="(result, index) in searchResults" :key="index"
               class="search-result-item"
               @click="selectSearchResult(result)">
            <div class="result-title">{{ result.name || result.address }}</div>
            <div class="result-address">{{ result.address }}</div>
          </div>
        </div>
        <div v-if="displayStations.length > 0 && showLists" class="station-list">
          <div class="station-list-header">
            <span>Nearby Rescue Stations ({{ displayStations.length }})</span>
          </div>
          <div v-for="(station, index) in displayStations" :key="index"
               class="station-item"
               @click="selectStation(station)">
            <div class="station-info">
              <div class="station-name">{{ station.name }}</div>
              <div class="station-address">{{ station.address }}</div>
              <div class="station-phone" v-if="station.phone">{{ station.phone }}</div>
            </div>
            <el-button type="success" size="small" @click.stop="startRescueForStation(station)">Rescue</el-button>
          </div>
        </div>
        </div>
        <el-button type="primary" @click="getCurrentLocation">
          <i class="el-icon-position"></i> My Location
        </el-button>
      </div>
      <div class="amap-container">
        <div id="amap"></div>
      </div>
      <div v-if="selectedLocation" class="location-info">
        <h3>Selected Location</h3>
        <p>{{ selectedLocation.name || selectedLocation.address }}</p>
        <el-button type="success" @click="startRescue">Initiate Rescue</el-button>
      </div>
    </div>

    <el-dialog :visible.sync="rescueDialogVisible" title="Submit Rescue Request" width="600px" @close="resetRescueForm">
      <el-form :model="rescueForm" label-width="140px" :rules="rescueFormRules" ref="rescueFormRef">
        <el-form-item label="Name" prop="name">
          <el-input v-model="rescueForm.name" disabled></el-input>
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
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="rescueDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitRescueForm" :loading="submitLoading">Submit</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getNearbyStations, allRescueStation } from '@/api/rescueStation'
import { insertReport } from '@/api/report'
import { geocode, searchNearbyPetHospitals } from '@/api/help'

export default {
  name: 'Help',
  data() {
    return {
      map: null,
      searchQuery: '',
      searchResults: [],
      showLists: false,
      selectedLocation: null,
      userLocation: {
        latitude: null,
        longitude: null
      },
      nearbyStations: [],
      displayStations: [],
      rescueDialogVisible: false,
      submitLoading: false,
      rescueForm: {
        name: '',
        location: '',
        latitude: '',
        longitude: '',
        birdName: '',
        species: '',
        injuryType: '',
        injuryDescription: ''
      },
      rescueFormRules: {
        birdName: [{ required: true, message: 'Please enter bird name', trigger: 'blur' }],
        injuryType: [{ required: true, message: 'Please select injury type', trigger: 'change' }],
        injuryDescription: [{ required: true, message: 'Please enter description', trigger: 'blur' }]
      },
      rescueFormRef: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.loadAMap()
    })
  },
  methods: {
    loadAMap() {
      if (typeof window.AMap === 'undefined') {
        const script = document.createElement('script')
        // Use the correct Web端 key from the user's account
        script.src = 'https://webapi.amap.com/maps?v=1.4.15&key=e0edd598435b472eda3656fad1055d37&lang=en'
        script.async = true
        script.onload = () => this.initAMap()
        script.onerror = (error) => {
          console.warn('Failed to load AMap script', error)
          this.$message.error('Failed to load map. Please check your network connection.')
        }
        document.head.appendChild(script)
      } else {
        this.initAMap()
      }
    },

    initAMap() {
      try {
        const container = document.getElementById('amap')
        if (!container) {
          console.error('Map container not found')
          return
        }

        this.map = new window.AMap.Map('amap', {
          center: [116.4074, 39.9042],
          zoom: 10,
          lang: 'en',
          viewMode: '2D',
          resizeEnable: true,
          zoomEnable: true,
        })

        if (this.map.setLang) {
          this.map.setLang('en')
        }

        this.map.on('complete', () => {
          console.log('Map loaded successfully')
          this.loadAllStations()
          this.getCurrentLocation()
        })

      } catch (error) {
        console.error('Error initializing map:', error)
      }
    },

    getCurrentLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            this.userLocation.latitude = position.coords.latitude
            this.userLocation.longitude = position.coords.longitude

            if (this.map) {
              this.map.setCenter([this.userLocation.longitude, this.userLocation.latitude])
              this.map.setZoom(15)

              // Add marker for user location
              const marker = new window.AMap.Marker({
                position: [this.userLocation.longitude, this.userLocation.latitude],
                title: 'Your Location',
                icon: new window.AMap.Icon({
                  size: new window.AMap.Size(30, 30),
                  image: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png',
                  imageSize: new window.AMap.Size(30, 30)
                })
              })
              marker.setMap(this.map)

              // Check nearby pet hospitals
              this.checkAndAddNearbyPetHospitals()
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

    handleSearch() {
      if (!this.searchQuery) {
        this.$message.warning('Please enter a location to search (English only)')
        return
      }

      const loadingInstance = this.$loading({
        lock: true,
        text: 'Searching location...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      if (this.map) {
        this.performSearch(loadingInstance)
      } else {
        loadingInstance.close()
        this.$message.error('Map not initialized')
      }
    },

    handleSearchBlur() {
      setTimeout(() => {
        this.showLists = false
      }, 200)
    },

    toggleLists() {
      this.showLists = !this.showLists
      if (this.showLists) {
        this.handleSearch()
      }
    },

    performSearch(loadingInstance) {
      try {
        console.log('Performing search for:', this.searchQuery)

        geocode(this.searchQuery)
          .then(response => {
            loadingInstance.close()
            console.log('Geocoder response:', response)

            if (response.code === 200 && response.data) {
              this.searchResults = response.data
              if (response.data.length === 1) {
                // Auto select if only one result
                this.selectSearchResult(response.data[0])
              } else {
                this.$message.success(`Found ${response.data.length} locations`)
              }
            } else {
              this.$message.error('Location not found. Please try a different address.')
              console.error('Geocoder error:', response)
            }
          })
          .catch(error => {
            loadingInstance.close()
            console.error('Error in performSearch:', error)
            this.$message.error('Error searching location. Please try again.')
          })
      } catch (error) {
        loadingInstance.close()
        console.error('Error in performSearch:', error)
        this.$message.error('Error searching location. Please try again.')
      }
    },

    selectSearchResult(result) {
      this.$message.success('Location selected')
      const position = [result.longitude, result.latitude]

      console.log('Selecting location:', result)
      console.log('Map instance:', this.map)
      console.log('Marker position:', position)

      if (!this.map) {
        console.error('Map is not initialized')
        this.$message.error('Map is not initialized')
        return
      }

      this.map.setCenter(position)
      this.map.setZoom(15)

      try {
        // Add marker for selected location
        const marker = new window.AMap.Marker({
          position: position,
          title: result.name || result.address,
          icon: new window.AMap.Icon({
            size: new window.AMap.Size(30, 30),
            image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
            imageSize: new window.AMap.Size(30, 30)
          })
        })

        console.log('Marker created:', marker)
        marker.setMap(this.map)
        console.log('Marker added to map')

        // Add English text label on the map
        const text = new window.AMap.Text({
          text: result.name || result.address,
          textAlign: 'center',
          backgroundColor: 'rgba(255, 255, 255, 0.8)',
          borderColor: '#ccc',
          borderWidth: 1,
          padding: '5px 10px',
          position: position,
          style: {
            'font-size': '14px',
            'color': '#333'
          }
        })
        text.setMap(this.map)

        // Add info window to marker
        const infoWindow = new window.AMap.InfoWindow({
          content: `
            <div style="padding: 10px;">
              <h3>${result.name}</h3>
              <p>${result.address}</p>
              ${result.phone ? `<p>Phone: ${result.phone}</p>` : ''}
              <p>Longitude: ${result.longitude}</p>
              <p>Latitude: ${result.latitude}</p>
              <button onclick="document.querySelector('.location-info .el-button--success').click()"
                      style="margin-top: 10px; padding: 5px 10px; background: #67c23a; color: white; border: none; border-radius: 4px; cursor: pointer;">
                Initiate Rescue
              </button>
            </div>
          `,
          offset: new window.AMap.Pixel(0, -30)
        })

        marker.on('click', () => {
          infoWindow.open(this.map, marker.getPosition())
        })

        this.selectedLocation = {
          name: result.name,
          address: result.address,
          longitude: result.longitude,
          latitude: result.latitude
        }

        // Clear search results
        this.searchResults = []

        // Search for nearby rescue stations
        this.searchNearbyStations(result.latitude, result.longitude)
      } catch (error) {
        console.error('Error adding marker:', error)
        this.$message.error('Error adding marker to map')
      }
    },

    searchNearbyStations(latitude, longitude) {
      getNearbyStations(latitude, longitude, 50)
        .then(response => {
          if (response.code === 200) {
            this.nearbyStations = response.data
            // Add markers for nearby stations
            this.addStationMarkers()
          }
        })
        .catch(error => {
          console.error('Error searching nearby stations:', error)
        })
    },

    addStationMarkers() {
      if (!this.map || this.nearbyStations.length === 0) return

      const self = this

      this.nearbyStations.forEach(station => {
        if (station.longitude && station.latitude) {
          const marker = new window.AMap.Marker({
            position: [station.longitude, station.latitude],
            title: station.name,
            extData: station,
            icon: new window.AMap.Icon({
              size: new window.AMap.Size(30, 30),
              image: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png',
              imageSize: new window.AMap.Size(30, 30)
            })
          })

          const infoWindow = new window.AMap.InfoWindow({
            content: `
              <div style="padding: 10px; min-width: 200px;">
                <h3>${station.name}</h3>
                <p>Address: ${station.address || 'N/A'}</p>
                <p>Phone: ${station.phone || 'N/A'}</p>
                <p>Hours: ${station.openingHours || 'N/A'}</p>
                <button class="rescue-btn" data-action="rescue" style="margin-top: 10px; padding: 5px 15px; background: #67c23a; color: white; border: none; border-radius: 4px; cursor: pointer;">
                  Initiate Rescue
                </button>
              </div>
            `,
            offset: new window.AMap.Pixel(0, -30)
          })

          marker.on('click', (e) => {
            infoWindow.open(self.map, marker.getPosition())

            setTimeout(() => {
              const btn = document.querySelector('.rescue-btn')
              if (btn) {
                btn.addEventListener('click', () => {
                  self.startRescueForStation(station)
                })
              }
            }, 100)
          })

          marker.setMap(self.map)
        }
      })
    },

    loadAllStations() {
      allRescueStation()
        .then(response => {
          if (response.code === 200 && response.data) {
            this.nearbyStations = response.data
            this.loadDisplayStations()
            this.addStationMarkers()
            console.log('Loaded all stations:', response.data.length)
          }
        })
        .catch(error => {
          console.error('Error loading all stations:', error)
        })
    },

    loadDisplayStations() {
      if (!this.userLocation.latitude || !this.userLocation.longitude) {
        this.displayStations = this.nearbyStations.slice(0, 20)
        return
      }

      const radius = 30000
      const stationsWithDistance = this.nearbyStations.map(station => {
        station.distance = this.calculateDistance(
          this.userLocation.latitude,
          this.userLocation.longitude,
          station.latitude,
          station.longitude
        )
        return station
      }).filter(station => station.distance <= radius)
        .sort((a, b) => a.distance - b.distance)

      if (stationsWithDistance.length < 10) {
        this.displayStations = stationsWithDistance.slice(0, 20)
      } else {
        this.displayStations = stationsWithDistance.slice(0, 20)
      }
    },

    selectStation(station) {
      if (!this.map) return

      this.selectedLocation = {
        name: station.name,
        address: station.address,
        latitude: station.latitude,
        longitude: station.longitude,
        id: station.id
      }

      this.map.setCenter([station.longitude, station.latitude])
      this.map.setZoom(15)
    },

    startRescueForStation(station) {
      this.selectedLocation = {
        name: station.name,
        address: station.address,
        latitude: station.latitude,
        longitude: station.longitude,
        id: station.id
      }
      this.startRescue()
    },

    checkAndAddNearbyPetHospitals() {
      const radius = 30000
      if (this.userLocation.latitude && this.userLocation.longitude) {
        const nearbyCount = this.nearbyStations.filter(station => {
          const distance = this.calculateDistance(
            this.userLocation.latitude,
            this.userLocation.longitude,
            station.latitude,
            station.longitude
          )
          return distance <= radius
        }).length

        if (nearbyCount < 10) {
          this.$message.info(`Found only ${nearbyCount} rescue stations nearby. Searching for pet hospitals...`)
          this.searchAndAddPetHospitals()
        }
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

    searchAndAddPetHospitals() {
      searchNearbyPetHospitals(this.userLocation.latitude, this.userLocation.longitude, 30000)
        .then(response => {
          if (response.code === 200 && response.data) {
            const count = response.data.length
            this.$message.success(`Found ${count} pet hospitals nearby`)
            this.nearbyStations = [...this.nearbyStations, ...response.data]
            this.addStationMarkers()
            this.loadDisplayStations()
          } else {
            console.log('No pet hospitals found or error:', response)
          }
        })
        .catch(error => {
          console.error('Error searching pet hospitals:', error)
        })
    },

    startRescue() {
      if (!this.selectedLocation) {
        this.$message.error('Please select a location first')
        return
      }

      this.rescueForm.name = this.selectedLocation.name
      this.rescueForm.location = this.selectedLocation.address
      this.rescueForm.latitude = this.selectedLocation.latitude
      this.rescueForm.longitude = this.selectedLocation.longitude
      this.rescueForm.birdName = ''
      this.rescueForm.species = ''
      this.rescueForm.injuryType = ''
      this.rescueForm.injuryDescription = ''
      this.rescueDialogVisible = true
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
        injuryDescription: ''
      }
      if (this.$refs.rescueFormRef) {
        this.$refs.rescueFormRef.resetFields()
      }
    },

    async submitRescueForm() {
      if (!this.rescueForm.birdName || !this.rescueForm.injuryType || !this.rescueForm.injuryDescription) {
        this.$message.error('Please fill all required fields')
        return
      }

      this.submitLoading = true
      try {
        let userId = parseInt(sessionStorage.getItem('id'))
        let rescueStationId = null

        if (this.nearbyStations && this.nearbyStations.length > 0) {
          for (const station of this.nearbyStations) {
            if (station.latitude === this.rescueForm.latitude &&
                station.longitude === this.rescueForm.longitude) {
              rescueStationId = station.id
              break
            }
          }
        }

        const reportData = {
          birdName: this.rescueForm.birdName,
          species: this.rescueForm.species || 'Unknown',
          name: this.rescueForm.name,
          location: this.rescueForm.location,
          latitude: this.rescueForm.latitude,
          longitude: this.rescueForm.longitude,
          injuryType: this.rescueForm.injuryType,
          injuryDescription: this.rescueForm.injuryDescription,
          submitterId: userId ? userId.toString() : null,
          rescueStationId: rescueStationId
        }

        const response = await insertReport(reportData)
        if (response.code === 200) {
          this.$message.success('Rescue report submitted successfully')
          this.rescueDialogVisible = false
          this.resetRescueForm()
        } else {
          this.$message.error(response.msg || 'Failed to submit rescue report')
        }
      } catch (error) {
        console.error('Error submitting rescue report:', error)
        this.$message.error('Failed to submit rescue report')
      } finally {
        this.submitLoading = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.help-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.help-header {
  text-align: center;
  padding: 20px;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 10;

  h1 {
    font-size: 28px;
    color: #333;
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: #666;
    margin: 0;
  }
}

.map-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  padding: 20px;
}

.map-controls {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;

  .search-box {
    flex: 1;
    min-width: 300px;
    position: relative;

    .search-results {
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      background: white;
      border: 1px solid #dcdfe6;
      border-top: none;
      border-radius: 0 0 4px 4px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      z-index: 100;
      max-height: 200px;
      overflow-y: auto;

      .search-result-item {
        padding: 10px 15px;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
          background-color: #f5f7fa;
        }

        &:active {
          background-color: #ecf5ff;
        }
      }
    }

    .station-list {
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      background: white;
      border: 1px solid #dcdfe6;
      border-top: none;
      border-radius: 0 0 4px 4px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      z-index: 99;
      max-height: 300px;
      overflow-y: auto;
      margin-top: 5px;

      .station-list-header {
        padding: 10px 15px;
        background: #f5f7fa;
        border-bottom: 1px solid #ebeef5;
        font-weight: bold;
        color: #606266;
      }

      .station-item {
        padding: 10px 15px;
        cursor: pointer;
        transition: background-color 0.3s;
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-bottom: 1px solid #f0f0f0;

        &:hover {
          background-color: #f5f7fa;
        }

        &:last-child {
          border-bottom: none;
        }

        .station-info {
          flex: 1;

          .station-name {
            font-weight: bold;
            color: #303133;
            margin-bottom: 4px;
          }

          .station-address {
            font-size: 12px;
            color: #909399;
            margin-bottom: 2px;
          }

          .station-phone {
            font-size: 12px;
            color: #67c23a;
          }
        }
      }
    }
  }
}

.amap-container {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  min-height: 600px;

  #amap {
    width: 100%;
    height: 100%;
  }
}

.location-info {
  margin-top: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  h3 {
    font-size: 18px;
    color: #333;
    margin-bottom: 10px;
  }

  p {
    color: #666;
    margin-bottom: 15px;
  }
}

@media (max-width: 768px) {
  .map-controls {
    flex-direction: column;

    .search-box {
      width: 100%;
      min-width: unset;
    }
  }

  .amap-container {
    min-height: 500px;
  }
}
</style>
