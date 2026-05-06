import request from '@/utils/request'

// Botanic Zoo API
const BOTANIC_API_BASE = 'https://botanic-zoo-api.vercel.app/api'

/**
 * Get random bird facts
 * @returns {Promise<Array>} List of bird facts
 */
export async function getRandomBirdFacts() {
  try {
    const response = await fetch(`${BOTANIC_API_BASE}/animals/bird`)
    const data = await response.json()
    return data
  } catch (error) {
    console.error('Failed to fetch bird facts:', error)
    return []
  }
}

/**
 * Get animal of the day (could be a bird)
 */
export async function getAnimalOfTheDay() {
  try {
    const response = await fetch(`${BOTANIC_API_BASE}/animals/today`)
    const data = await response.json()
    return data
  } catch (error) {
    console.error('Failed to fetch animal of the day:', error)
    return null
  }
}

/**
 * Get specific bird by name
 * @param {string} birdName - The bird name to search for
 */
export async function getBirdFactByName(birdName) {
  try {
    const response = await fetch(`${BOTANIC_API_BASE}/animals/${encodeURIComponent(birdName)}`)
    const data = await response.json()
    return data
  } catch (error) {
    console.error(`Failed to fetch fact for ${birdName}:`, error)
    return null
  }
}

/**
 * Get a list of birds (pets category includes birds)
 */
export async function getBirdList() {
  try {
    // Pet list includes birds in the pets category
    const response = await fetch(`${BOTANIC_API_BASE}/pets`)
    const data = await response.json()
    // Filter for birds if the API provides category info
    return data
  } catch (error) {
    console.error('Failed to fetch bird list:', error)
    return []
  }
}
