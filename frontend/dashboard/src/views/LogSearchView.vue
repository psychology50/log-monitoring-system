<template>
  <div class="log-search">
    <h1>로그 검색</h1>
    <SearchForm/>
    <LogTable/>
  </div>
</template>

<script>
import {onMounted} from 'vue'
import SearchForm from '@/components/SearchForm.vue'
import LogTable from '@/components/LogTable.vue'
import {useLogStore} from '@/stores/logStore'

export default {
  name: 'LogSearchView',
  components: {
    SearchForm,
    LogTable
  },
  setup() {
    const logStore = useLogStore()

    onMounted(() => {
      // 페이지 로드 시 최근 로그 자동 로드
      logStore.loadRecentLogs()
    })

    return {}
  }
}
</script>

<style scoped>
.log-search {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  color: #333;
  margin-bottom: 30px;
}
</style>