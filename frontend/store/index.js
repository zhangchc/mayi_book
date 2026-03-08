import { createStore } from 'vuex'

const store = createStore({
  state: {
    userInfo: null,
    token: '',
    currentMonth: '',
    recordList: []
  },
  
  mutations: {
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      uni.setStorageSync('userInfo', userInfo)
    },
    
    SET_TOKEN(state, token) {
      state.token = token
      uni.setStorageSync('token', token)
    },
    
    SET_CURRENT_MONTH(state, month) {
      state.currentMonth = month
    },
    
    SET_RECORD_LIST(state, list) {
      state.recordList = list
    },
    
    CLEAR_USER_INFO(state) {
      state.userInfo = null
      state.token = ''
      uni.removeStorageSync('userInfo')
      uni.removeStorageSync('token')
    }
  },
  
  actions: {
    login({ commit }, { token, userInfo }) {
      commit('SET_TOKEN', token)
      commit('SET_USER_INFO', userInfo)
    },
    
    logout({ commit }) {
      commit('CLEAR_USER_INFO')
    }
  }
})

export default store
