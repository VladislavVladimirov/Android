package com.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.netology.nmedia.dto.User
import com.netology.nmedia.model.StateModel
import com.netology.nmedia.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    val data: LiveData<List<User>> = userRepository.data.asLiveData(Dispatchers.Default)
    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _userIdSet = MutableLiveData<Set<Int>>()
    val userIdSet: LiveData<Set<Int>>
        get() = _userIdSet

    init {
        getUserList()
    }

    private fun getUserList() = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            userRepository.getAll()
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun getUserIdList(set: Set<Int>) =
        viewModelScope.launch { _userIdSet.value = set }

    fun  getUserById(id: Int) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            _user.value = userRepository.getUserById(id)
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

}