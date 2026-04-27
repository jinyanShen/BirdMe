<template>
  <div class="identification-page">
    <div class="container">
      <div class="identification-container">
        <div class="upload-section">
          <div class="section-header">
            <h3>Bird Image Recognition</h3>
            <p>Upload a bird photo to identify its species</p>
          </div>

          <!-- Upload Component -->
          <UploadImg
            @image-uploaded="handleImageUploaded"
          />

          <!-- Identification Result -->
          <div v-if="form.name" class="result-section">
            <h4>Identification Result</h4>
            <div class="result-card">
              <div class="bird-icon">
                <i class="el-icon-star-on"></i>
              </div>
              <h5 class="bird-name">{{ form.name }}</h5>
              <p class="bird-description">{{ form.description }}</p>
              <div class="protection-level" v-if="form.protectionLevel">
                <span class="label">Protection Level:</span>
                <span class="value">{{ form.protectionLevel }}</span>
              </div>
              <div class="scientific-name" v-if="form.scientificName">
                <span class="label">Scientific Name:</span>
                <span class="value">{{ form.scientificName }}</span>
              </div>
              <div class="habitat" v-if="form.habitat">
                <span class="label">Habitat:</span>
                <span class="value">{{ form.habitat }}</span>
              </div>
              <div class="action-buttons">
                <el-button
                  type="primary"
                  size="small"
                  @click="viewMigrationRoute"
                  icon="el-icon-location"
                >
                  View Migration Route
                </el-button>
                <el-button
                  type="success"
                  size="small"
                  @click="goToRescue"
                  icon="el-icon-help"
                >
                  Find Rescue Stations
                </el-button>
              </div>
            </div>
          </div>

          <!-- Wikipedia Section -->
          <div v-if="form.name" class="wikipedia-section">
            <div class="section-header">
              <h4>
                <i class="el-icon-reading"></i>
                Wikipedia Knowledge
              </h4>
              <a v-if="wikiData && wikiData.url" :href="wikiData.url" target="_blank" class="wiki-link">
                Read more on Wikipedia <i class="el-icon-link"></i>
              </a>
            </div>

            <!-- Loading state -->
            <div v-if="loadingWiki" class="wiki-card loading">
              <div class="loading-content">
                <i class="el-icon-loading"></i>
                <span>Loading Wikipedia information...</span>
              </div>
            </div>

            <!-- Wikipedia content -->
            <div v-else-if="wikiData && !wikiError" class="wiki-card">
              <div class="wiki-summary">
                <img v-if="wikiData.thumbnail" :src="wikiData.thumbnail" class="wiki-image" :alt="wikiData.title">
                <div class="wiki-text">
                  <p>{{ wikiData.extract }}</p>
                </div>
              </div>
              <div v-if="wikiData.categories && wikiData.categories.length" class="wiki-categories">
                <span class="category-label">Related Categories:</span>
                <el-tag
                  v-for="cat in wikiData.categories.slice(0, 6)"
                  :key="cat"
                  size="small"
                  class="category-tag"
                >
                  {{ cat }}
                </el-tag>
              </div>
            </div>

            <!-- Notice when Wikipedia information is not available -->
            <div v-else-if="wikiError" class="wiki-card error">
              <div class="error-content">
                <i class="el-icon-warning"></i>
                <span>Wikipedia information is not available for this species.</span>
                <p class="error-hint">You can try searching manually on Wikipedia.</p>
              </div>
            </div>
          </div>

          <!-- Recognition Tips -->
          <div class="tips-section">
            <h4>Recognition Tips</h4>
            <ul>
              <li>📸 Upload clear, well-lit photos</li>
              <li>🐦 Focus on the bird's distinctive features</li>
              <li>🌿 Include the whole bird if possible</li>
              <li>🎯 Multiple angles increase accuracy</li>
            </ul>
          </div>
        </div>

        <!-- Rescue Station Section -->
        <div v-if="form.name" class="rescue-section">
          <h4>Nearby Rescue Stations</h4>
          <div class="rescue-card">
            <div class="location-input">
              <el-input
                v-model="userLocation"
                placeholder="Enter your location or enable GPS"
              >
                <el-button slot="append" icon="el-icon-position" @click="getCurrentLocation"></el-button>
              </el-input>
            </div>
            <el-button type="primary" @click="searchNearbyStations" class="search-button">
              Find Rescue Stations
            </el-button>
            <div v-if="nearbyStations.length > 0" class="stations-list">
              <div v-for="station in nearbyStations" :key="station.id" class="station-item">
                <h6>{{ station.name }}</h6>
                <p>{{ station.address }}</p>
                <p>Phone: {{ station.phone }}</p>
                <el-button type="info" size="small" @click="selectRescueStation(station)">
                  Select
                </el-button>
              </div>
            </div>
            <div v-else-if="searchingStations" class="loading">
              <el-icon class="is-loading"><i class="el-icon-loading"></i></el-icon> Searching...
            </div>
          </div>
        </div>

        <!-- Report Generation -->
        <div v-if="form.name && selectedStation" class="report-section">
          <h4>Generate Rescue Report</h4>
          <div class="report-card">
            <el-form :model="reportForm" label-width="100px">
              <el-form-item label="Injury Type">
                <el-select v-model="reportForm.injuryType" placeholder="Select injury type">
                  <el-option label="Broken Wing" value="Broken Wing"></el-option>
                  <el-option label="Injured Leg" value="Injured Leg"></el-option>
                  <el-option label="Illness" value="Illness"></el-option>
                  <el-option label="Other" value="Other"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="Description">
                <el-input
                  v-model="reportForm.injuryDescription"
                  type="textarea"
                  placeholder="Describe the bird's condition"
                  rows="3"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitReport">
                  Submit Report
                </el-button>
              </el-form-item>
            </el-form>
            <div v-if="reportSubmitted" class="report-success">
              <el-alert
                title="Report submitted successfully!"
                type="success"
                :closable="false"
              >
                <div slot="default">
                  <p>Your report has been sent to {{ selectedStation.name }}</p>
                  <p>They will contact you soon.</p>
                  <div class="report-actions">
                    <el-button type="text" @click="goToMyReports">
                      View My Reports
                    </el-button>
                  </div>
                </div>
              </el-alert>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UploadImg from '@/components/UploadImg/index.vue';
import { getNearbyStations } from '@/api/rescueStation';
import { identifyBird } from '@/api/file';
import { submitReport } from '@/api/report';

export default {
  name: 'BirdIdentification',
  components: {
    UploadImg
  },
  data() {
    return {
      imageSrc: '',
      form: {},
      userLocation: '',
      nearbyStations: [],
      searchingStations: false,
      selectedStation: null,
      reportForm: {
        injuryType: '',
        injuryDescription: ''
      },
      reportSubmitted: false,
      userLatitude: null,
      userLongitude: null,
      userId: sessionStorage.getItem("id"),
      // Wikipedia data
      wikiData: null,
      loadingWiki: false,
      wikiError: false,
      wikiDebounceTimer: null
    }
  },
  methods: {
    handleImageUploaded(data) {
      const { file, imageSrc } = data;
      this.imageSrc = imageSrc;
      this.processFile(file);
    },

    processFile(file) {
      if (file) {
        identifyBird(file).then(data => {
          if(data.code === 200){
            this.form = data.data
            this.$message.success(`Identified as: ${this.form.name}`);

            // Reset Wikipedia data
            this.wikiData = null;
            this.wikiError = false;

            // Fetch Wikipedia information
            this.fetchWikipediaInfo(this.form.name);
          } else {
            this.$message.error(data.msg);
          }
        })
          .catch(error => {
            this.$message.error("Identification failed");
          });
      } else {
        this.$message.error("Please upload image");
      }
    },

    /**
     * Fetch Wikipedia information
     */
    async fetchWikipediaInfo(birdName) {
      // Clear previous timer
      if (this.wikiDebounceTimer) {
        clearTimeout(this.wikiDebounceTimer);
      }

      this.loadingWiki = true;
      this.wikiError = false;
      this.wikiData = null;

      // Debounce: delay 500ms before request
      this.wikiDebounceTimer = setTimeout(async () => {
        try {
          // Build search query
          const searchQuery = `${birdName} bird`;

          // Use Wikipedia API
          const apiUrl = `https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(birdName)}`;
          const searchUrl = `https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=${encodeURIComponent(searchQuery)}&format=json&origin=*`;

          // Try to get page directly first
          const directResponse = await fetch(apiUrl);

          if (directResponse.ok) {
            const data = await directResponse.json();
            if (data.extract && !data.missing) {
              this.wikiData = {
                title: data.title,
                extract: data.extract,
                url: data.content_urls?.desktop?.page || `https://en.wikipedia.org/wiki/${encodeURIComponent(birdName)}`,
                thumbnail: data.thumbnail?.source || null,
                categories: []
              };

              // Fetch category information
              await this.fetchWikiCategories(birdName);
              this.loadingWiki = false;
              return;
            }
          }

          // If direct fetch fails, try search
          const searchResponse = await fetch(searchUrl);
          const searchData = await searchResponse.json();

          if (searchData.query && searchData.query.search && searchData.query.search.length > 0) {
            const topResult = searchData.query.search[0];
            const pageUrl = `https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(topResult.title)}`;
            const pageResponse = await fetch(pageUrl);
            const pageData = await pageResponse.json();

            if (pageData.extract) {
              this.wikiData = {
                title: pageData.title,
                extract: pageData.extract,
                url: pageData.content_urls?.desktop?.page || `https://en.wikipedia.org/wiki/${encodeURIComponent(topResult.title)}`,
                thumbnail: pageData.thumbnail?.source || null,
                categories: []
              };

              await this.fetchWikiCategories(topResult.title);
            } else {
              this.wikiError = true;
            }
          } else {
            this.wikiError = true;
          }
        } catch (error) {
          console.error('Error fetching Wikipedia info:', error);
          this.wikiError = true;
        } finally {
          this.loadingWiki = false;
        }
      }, 500);
    },

    /**
     * Get Wikipedia category information
     */
    async fetchWikiCategories(pageTitle) {
      try {
        const categoriesUrl = `https://en.wikipedia.org/w/api.php?action=query&titles=${encodeURIComponent(pageTitle)}&prop=categories&cllimit=10&format=json&origin=*`;
        const response = await fetch(categoriesUrl);
        const data = await response.json();

        const pages = data.query.pages;
        const page = Object.values(pages)[0];

        if (page && page.categories) {
          this.wikiData.categories = page.categories
            .map(cat => cat.title.replace('Category:', ''))
            .filter(cat => !cat.includes('Articles') && !cat.includes('CS1') && !cat.includes('Webarchive'));
        }
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    },

    viewMigrationRoute() {
      this.$router.push({
        path: '/knowledge/migration',
        query: { bird: this.form.name }
      });
    },

    goToRescue() {
      const rescueSection = document.querySelector('.rescue-section');
      if (rescueSection) {
        rescueSection.scrollIntoView({ behavior: 'smooth' });
      }
    },

    getCurrentLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            this.userLatitude = position.coords.latitude;
            this.userLongitude = position.coords.longitude;
            this.userLocation = `${this.userLatitude.toFixed(6)}, ${this.userLongitude.toFixed(6)}`;
            this.$message.success('Location obtained successfully');
          },
          (error) => {
            console.error('Error getting location:', error);
            this.$message.error('Failed to get location. Please enter your location manually.');
          }
        );
      } else {
        this.$message.error('Geolocation is not supported by your browser.');
      }
    },

    searchNearbyStations() {
      if (!this.userLatitude || !this.userLongitude) {
        this.userLatitude = 40.7128;
        this.userLongitude = -74.0060;
      }

      this.searchingStations = true;

      getNearbyStations(this.userLatitude, this.userLongitude, 50)
        .then(response => {
          if (response.code === 200) {
            this.nearbyStations = response.data;
          } else {
            this.$message.error('Failed to search rescue stations');
          }
          this.searchingStations = false;
        })
        .catch(error => {
          console.error('Error searching rescue stations:', error);
          this.$message.error('Failed to search rescue stations');
          this.searchingStations = false;
        });
    },

    selectRescueStation(station) {
      this.selectedStation = station;
      this.$message.success(`Selected ${station.name}`);
    },

    submitReport() {
      if (!this.selectedStation || !this.reportForm.injuryType) {
        this.$message.error('Please select a rescue station and injury type');
        return;
      }

      const reportData = {
        birdName: this.form.name,
        species: this.form.species || this.form.name,
        latitude: this.userLatitude || 40.7128,
        longitude: this.userLongitude || -74.0060,
        location: this.userLocation,
        injuryType: this.reportForm.injuryType,
        injuryDescription: this.reportForm.injuryDescription,
        imageUrl: this.imageSrc,
        submitterId: this.userId,
        rescueStationId: this.selectedStation.id
      };

      submitReport(reportData)
        .then(data => {
          if (data.code === 200) {
            this.reportSubmitted = true;
            this.$message.success('Report submitted successfully');
          } else {
            this.$message.error('Failed to submit report');
          }
        })
        .catch(error => {
          console.error('Error submitting report:', error);
          this.$message.error('Failed to submit report');
        });
    },

    goToMyReports() {
      this.$router.push('/report');
    }
  },

  mounted() {
    if (this.$route.query.bird) {
      console.log('Bird from query:', this.$route.query.bird);
    }
  },

  beforeDestroy() {
    if (this.wikiDebounceTimer) {
      clearTimeout(this.wikiDebounceTimer);
    }
  }
}
</script>

<style scoped>
.identification-page {
  padding-top: 70px;
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  min-height: 100vh;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.identification-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 60px;
}

.upload-section {
  padding: 30px;
  border-bottom: 1px solid #eaeaea;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.section-header h3,
.section-header h4 {
  margin: 0;
  font-size: 18px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header h4 i {
  color: #22b3c1;
  font-size: 20px;
}

.wiki-link {
  color: #22b3c1;
  text-decoration: none;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s;
}

.wiki-link:hover {
  color: #1a9aa8;
  text-decoration: underline;
}

.result-section {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #eaeaea;
}

.result-section h4 {
  color: #333;
  margin: 0 0 20px 0;
  font-size: 18px;
}

.result-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  position: relative;
}

.result-card .bird-icon {
  font-size: 48px;
  color: #22b3c1;
  margin-bottom: 15px;
}

.result-card .bird-name {
  font-size: 24px;
  font-weight: 600;
  color: #22b3c1;
  margin: 0 0 15px 0;
}

.result-card .bird-description {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 20px 0;
}

.result-card .protection-level,
.result-card .scientific-name,
.result-card .habitat {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 13px;
  padding: 5px 0;
}

.result-card .protection-level .label,
.result-card .scientific-name .label,
.result-card .habitat .label {
  color: #8492a6;
  font-weight: 500;
}

.result-card .protection-level .value,
.result-card .scientific-name .value,
.result-card .habitat .value {
  color: #333;
  text-align: right;
  flex: 1;
  margin-left: 10px;
}

.result-card .action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: center;
}

/* Wikipedia Section */
.wikipedia-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eaeaea;
}

.wikipedia-section .section-header {
  margin-bottom: 15px;
}

.wiki-card {
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.wiki-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.wiki-card.loading,
.wiki-card.error {
  min-height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-content,
.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #999;
  text-align: center;
}

.loading-content i,
.error-content i {
  font-size: 32px;
}

.error-content .error-hint {
  font-size: 12px;
  color: #ccc;
  margin-top: 8px;
}

.wiki-summary {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.wiki-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.wiki-text {
  flex: 1;
}

.wiki-text p {
  margin: 0;
  line-height: 1.6;
  color: #555;
  font-size: 14px;
}

.wiki-categories {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #eaeaea;
}

.category-label {
  font-size: 13px;
  color: #8492a6;
  margin-right: 8px;
}

.category-tag {
  background: #e0f7fa;
  border: none;
  color: #22b3c1;
}

/* Tips Section */
.tips-section {
  margin-top: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.tips-section h4 {
  color: #333;
  margin: 0 0 15px 0;
  font-size: 16px;
}

.tips-section ul {
  margin: 0;
  padding-left: 20px;
}

.tips-section li {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
  line-height: 1.5;
}

/* Rescue Section */
.rescue-section {
  padding: 30px;
  border-bottom: 1px solid #eaeaea;
}

.rescue-section h4 {
  color: #333;
  margin: 0 0 20px 0;
  font-size: 18px;
}

.rescue-card {
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  padding: 20px;
}

.location-input {
  margin-bottom: 15px;
}

.search-button {
  width: 100%;
  margin-bottom: 15px;
}

.stations-list {
  margin-top: 15px;
}

.station-item {
  padding: 12px;
  border: 1px solid #eaeaea;
  border-radius: 6px;
  margin-bottom: 10px;
}

.station-item h6 {
  margin: 0 0 5px 0;
  color: #22b3c1;
  font-size: 14px;
}

.station-item p {
  margin: 0 0 5px 0;
  font-size: 13px;
  color: #666;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}

/* Report Section */
.report-section {
  padding: 30px;
}

.report-section h4 {
  color: #333;
  margin: 0 0 20px 0;
  font-size: 18px;
}

.report-card {
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  padding: 20px;
}

.report-success {
  margin-top: 15px;
}

.report-actions {
  margin-top: 15px;
  text-align: center;
}

/* Responsive Design */
@media (max-width: 768px) {
  .identification-page {
    padding-top: 60px;
  }

  .upload-section,
  .rescue-section,
  .report-section {
    padding: 20px;
  }

  .result-card {
    padding: 20px;
  }

  .result-card .bird-name {
    font-size: 20px;
  }

  .result-card .action-buttons {
    flex-direction: column;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .wiki-summary {
    flex-direction: column;
  }

  .wiki-image {
    width: 100%;
    height: 160px;
  }

  .wiki-categories {
    flex-direction: column;
    align-items: flex-start;
  }

  .category-label {
    margin-bottom: 8px;
  }
}
</style>
