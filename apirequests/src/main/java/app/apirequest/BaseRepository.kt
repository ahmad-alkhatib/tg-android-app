package app.apirequest

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class BaseRepository @Inject constructor(private val client: APIClient) {
    private var service: WebService = client.setup()

    internal fun processError(t: Throwable): ApiResponse<*> {
        if (t is UnknownHostException) {
            return ApiResponse.networkError()
        }
        return if (t is SocketTimeoutException) {
            ApiResponse.timeoutError()
        } else ApiResponse.technicalError()
    }

//    fun login(user: AuthRequest): MutableLiveData<ApiResponse<AuthResponse>> {
//        val data = MutableLiveData<ApiResponse<AuthResponse>>()
//        service.login(user).enqueue(object : Callback<AuthResponse> {
//            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<AuthResponse>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<AuthResponse>)
//            }
//        })
//        return data
//    }
//
//    fun register(user: AuthRequest): MutableLiveData<ApiResponse<AuthResponse>> {
//        val data = MutableLiveData<ApiResponse<AuthResponse>>()
//        service.register(user).enqueue(object : Callback<AuthResponse> {
//
//            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<AuthResponse>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<AuthResponse>)
//            }
//        })
//        return data
//    }
//
//    fun forgotPassword(user: AuthRequest): MutableLiveData<ApiResponse<AuthResponse>> {
//        val data = MutableLiveData<ApiResponse<AuthResponse>>()
//        service.forgotPassword(user).enqueue(object : Callback<AuthResponse> {
//
//            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<AuthResponse>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<AuthResponse>)
//            }
//        })
//        return data
//    }
//
//    fun getProfile(): MutableLiveData<ApiResponse<UserProfileResponse>> {
//        val data = MutableLiveData<ApiResponse<UserProfileResponse>>()
//        service.getProfile().enqueue(object : Callback<UserProfileResponse> {
//
//            override fun onResponse(
//                call: Call<UserProfileResponse>,
//                response: Response<UserProfileResponse>
//            ) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<UserProfileResponse>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<UserProfileResponse>)
//            }
//        })
//        return data
//    }
//
//    fun updateProfile(userProfileRequest: UserProfileRequest): MutableLiveData<ApiResponse<UserProfileResponse>> {
//        val data = MutableLiveData<ApiResponse<UserProfileResponse>>()
//        service.updateProfile(userProfileRequest).enqueue(object : Callback<UserProfileResponse> {
//
//            override fun onResponse(
//                call: Call<UserProfileResponse>,
//                response: Response<UserProfileResponse>
//            ) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<UserProfileResponse>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<UserProfileResponse>)
//            }
//        })
//        return data
//    }
//
//    fun getHome(locale: String): MutableLiveData<ApiResponse<HomeResponse>> {
//        val data = MutableLiveData<ApiResponse<HomeResponse>>()
//        service.getHome(locale).enqueue(object : Callback<HomeResponse> {
//
//            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<HomeResponse>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<HomeResponse>)
//            }
//        })
//        return data
//    }
//
//
//    fun getBins(details: BinDetails): MutableLiveData<ApiResponse<BinResponse>> {
//        val data = MutableLiveData<ApiResponse<BinResponse>>()
//
//        service.getBins(details).enqueue(object : Callback<BinResponse> {
//
//            override fun onResponse(call: Call<BinResponse>, response: Response<BinResponse>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<BinResponse>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<BinResponse>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<BinResponse>)
//            }
//        })
//        return data
//    }
//
//    fun getBinDetails(binDetails: BinDetails): MutableLiveData<ApiResponse<BinDetails>> {
//        val data = MutableLiveData<ApiResponse<BinDetails>>()
//        service.getBinDetails(binDetails).enqueue(object : Callback<BinDetails> {
//
//            override fun onResponse(call: Call<BinDetails>, response: Response<BinDetails>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<BinDetails>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<BinDetails>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<BinDetails>)
//            }
//        })
//        return data
//    }
//
//    fun favoriteBin(bin: Bin): MutableLiveData<ApiResponse<Bin>> {
//        val data = MutableLiveData<ApiResponse<Bin>>()
//        service.favoriteBin(bin).enqueue(object : Callback<Bin> {
//
//            override fun onResponse(call: Call<Bin>, response: Response<Bin>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<Bin>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<Bin>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<Bin>)
//            }
//        })
//        return data
//    }
//
//    fun getContribution(localStr: String): MutableLiveData<ApiResponse<ContributionResponse>> {
//        val data = MutableLiveData<ApiResponse<ContributionResponse>>()
//        service.contribution(localStr).enqueue(object : Callback<ContributionResponse> {
//
//            override fun onResponse(call: Call<ContributionResponse>, response: Response<ContributionResponse>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<ContributionResponse>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<ContributionResponse>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<ContributionResponse>)
//            }
//        })
//        return data
//    }
//
//    fun report(request: ReportRequest): MutableLiveData<ApiResponse<ReportStat>> {
//        val data = MutableLiveData<ApiResponse<ReportStat>>()
//        service.reportRequest(request).enqueue(object : Callback<ReportStat> {
//
//            override fun onResponse(call: Call<ReportStat>, response: Response<ReportStat>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<ReportStat>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<ReportStat>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<ReportStat>)
//            }
//        })
//        return data
//    }
//
//    fun getReportOptions(request: ReportRequest): MutableLiveData<ApiResponse<ReportResponse>> {
//        val data = MutableLiveData<ApiResponse<ReportResponse>>()
//        service.reportOptions(request).enqueue(object : Callback<ReportResponse> {
//
//            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<ReportResponse>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<ReportResponse>)
//            }
//        })
//        return data
//    }
//
//    fun translations(translationRequest: TranslationRequest): LiveData<ApiResponse<Translation>> {
//        val data = MutableLiveData<ApiResponse<Translation>>()
//        service.translations(translationRequest.locale, translationRequest.projectId)
//            .enqueue(object : Callback<Translation> {
//                override fun onResponse(call: Call<Translation>, response: Response<Translation>?) {
//                    if (response != null && response.isSuccessful && response.body() != null) {
//                        data.postValue(
//                            ApiResponse.success(
//                                response.body()!!
//                            )
//                        )
//                    } else {
//                        data.postValue(
//                            ApiResponse.error(
//                                response
//                            ) as ApiResponse<Translation>
//                        )
//                    }
//                }
//
//                override fun onFailure(call: Call<Translation>, t: Throwable) {
//                    data.postValue(processError(t) as ApiResponse<Translation>)
//                }
//            })
//        return data
//    }
//
//    fun getDispose(request: BinDisposeRequest): MutableLiveData<ApiResponse<BinDisposeResponse>> {
//        val data = MutableLiveData<ApiResponse<BinDisposeResponse>>()
//        service.disposeBinDetails(request).enqueue(object : Callback<BinDisposeResponse> {
//
//            override fun onResponse(call: Call<BinDisposeResponse>, response: Response<BinDisposeResponse>) {
//                if (response.isSuccessful) {
//                    data.postValue(
//                        ApiResponse.success(
//                            response.body()!!
//                        )
//                    )
//                } else {
//                    data.postValue(
//                        ApiResponse.error(
//                            response
//                        ) as ApiResponse<BinDisposeResponse>
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<BinDisposeResponse>, t: Throwable) {
//                data.postValue(processError(t) as ApiResponse<BinDisposeResponse>)
//            }
//        })
//        return data
//    }
}