<template>
  <div class="help-container">
    <div class="help-header">
      <h1>Help Center</h1>
      <p>Get assistance with bird rescue and reporting</p>
    </div>
    <div class="amap-container">
      <div id="amap"></div>
    </div>
    <div class="rescue-stations-list">
      <h2>Rescue Stations</h2>
      <div v-if="loadingStations" class="loading">Loading rescue stations...</div>
      <div v-else-if="rescueStations.length === 0" class="no-stations">No rescue stations found</div>
      <div v-else class="stations-grid">
        <div v-for="station in rescueStations" :key="station.id" class="station-card">
          <h3>{{ station.name }}</h3>
          <p><strong>Address:</strong> {{ station.address }}</p>
          <p><strong>Phone:</strong> {{ station.phone }}</p>
          <p><strong>Hours:</strong> {{ station.openingHours }}</p>
          <button class="view-on-map" @click="centerOnStation(station)">View on Map</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { allRescueStation } from '@/api/rescueStation'

export default {
  name: 'Help',
  data() {
    return {
      rescueStations: [],
      loadingStations: false,
      map: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.loadAMap()
      this.fetchRescueStations()
    })
  },
  methods: {
    loadAMap() {
      // 先尝试加载国际版
      if (typeof window.AMap === 'undefined') {
        const script = document.createElement('script')
        script.src = 'https://webapi.amap.com/maps?v=1.4.15&key=e5d66bf0765f5f7cb43d9390902bdab2&lang=en'
        script.async = true
        script.onload = () => this.initAMap()
        script.onerror = (error) => {
          console.warn('International version failed, trying standard version...', error)
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

        // 尝试创建地图
        this.map = new window.AMap.Map('amap', {
          center: [116.4074, 39.9042],
          zoom: 10,
          lang: 'en',  // 设置语言
          viewMode: '2D',
          resizeEnable: true,
          zoomEnable: true,
        })

        // 多次尝试设置语言
        if (this.map.setLang) {
          this.map.setLang('en')
        }

        // 监听地图加载完成
        this.map.on('complete', () => {
          console.log('Map loaded, current lang:', this.map.getLang ? this.map.getLang() : 'unknown')
          // 地图加载完成后添加标记
          this.addStationMarkers()
        })

      } catch (error) {
        console.error('Error initializing map:', error)
      }
    },

    fetchRescueStations() {
      this.loadingStations = true
      allRescueStation()
        .then(response => {
          if (response.code === 200) {
            this.rescueStations = response.data
            // 数据加载完成后添加标记
            this.addStationMarkers()
          } else {
            console.error('Failed to fetch rescue stations:', response.msg)
          }
        })
        .catch(error => {
          console.error('Error fetching rescue stations:', error)
        })
        .finally(() => {
          this.loadingStations = false
        })
    },

    addStationMarkers() {
      if (!this.map || this.rescueStations.length === 0) return

      this.rescueStations.forEach(station => {
        if (station.longitude && station.latitude) {
          const marker = new window.AMap.Marker({
            position: [station.longitude, station.latitude],
            title: station.name,
            icon: new window.AMap.Icon({
              size: new window.AMap.Size(30, 30),
              image: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png',
              imageSize: new window.AMap.Size(30, 30)
            })
          })

          // 添加信息窗口
          const infoWindow = new window.AMap.InfoWindow({
            content: `
              <div style="padding: 10px;">
                <h3>${station.name}</h3>
                <p>Address: ${station.address}</p>
                <p>Phone: ${station.phone}</p>
                <p>Hours: ${station.openingHours}</p>
              </div>
            `,
            offset: new window.AMap.Pixel(0, -30)
          })

          marker.on('click', () => {
            infoWindow.open(this.map, marker.getPosition())
          })

          marker.setMap(this.map)
        }
      })
    },

    centerOnStation(station) {
      if (!this.map || !station.longitude || !station.latitude) return

      this.map.setCenter([station.longitude, station.latitude])
      this.map.setZoom(15)
    }
  }
}
</script>

<style scoped lang="scss">
.help-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.help-header {
  text-align: center;
  margin-bottom: 30px;

  h1 {
    font-size: 32px;
    color: #333;
    margin-bottom: 10px;
  }

  p {
    font-size: 16px;
    color: #666;
  }
}

.amap-container {
  width: 100%;
  height: 600px;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 30px;
  background-color: #f5f5f5;

  #amap {
    width: 100%;
    height: 100%;
  }
}

.rescue-stations-list {
  margin-top: 40px;
  
  h2 {
    font-size: 24px;
    color: #333;
    margin-bottom: 20px;
    text-align: center;
  }
  
  .loading,
  .no-stations {
    text-align: center;
    padding: 40px;
    color: #666;
  }
  
  .stations-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    margin-top: 20px;
  }
  
  .station-card {
    background: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    
    h3 {
      font-size: 18px;
      color: #333;
      margin-bottom: 10px;
    }
    
    p {
      font-size: 14px;
      color: #666;
      margin-bottom: 8px;
    }
    
    .view-on-map {
      margin-top: 15px;
      padding: 8px 16px;
      background-color: #1890ff;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 14px;
      
      &:hover {
        background-color: #40a9ff;
      }
    }
  }
}
</style>