package com.example.labproject3.data.request

data class UserUpdateRequest(
	val id: String,
	val nickName: String,
	val email: String,
	val avatarLink: String,
	val name: String,
	val birthDate: String,
	val gender: Int,
)

