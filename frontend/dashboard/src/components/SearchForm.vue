<template>
  <div class="search-form">
    <div class="search-container">
      <input
          v-model="searchQuery"
          class="search-input"
          placeholder="로그 검색... (Enter로 검색)"
          type="text"
          @keyup.enter="handleSearch"
      />
      <button class="search-button" @click="handleSearch">
        검색
      </button>
      <button class="recent-button" @click="handleLoadRecent">
        최신 로그
      </button>
    </div>
  </div>
</template>

<script>
import {ref} from 'vue'
import {useLogStore} from '@/stores/logStore'

export default {
  name: 'SearchForm',
  setup() {
    const logStore = useLogStore()
    const searchQuery = ref('')

    const handleSearch = () => {
      logStore.searchLogs(searchQuery.value, 0)
    }

    const handleLoadRecent = () => {
      searchQuery.value = ''
      logStore.loadRecentLogs()
    }

    return {
      searchQuery,
      handleSearch,
      handleLoadRecent
    }
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
}

.search-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.search-button, .recent-button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-button:hover, .recent-button:hover {
  background-color: #0056b3;
}

.recent-button {
  background-color: #28a745;
}

.recent-button:hover {
  background-color: #1e7e34;
}
</style>