package com.example.spacekuma.view_models.signup

import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spacekuma.data.Check_Model
import com.example.spacekuma.data.Login_Model
import com.example.spacekuma.data.MyInfo_Model
import com.example.spacekuma.data.User_Model
import com.example.spacekuma.retrofit.ApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SignUPViewModel : ViewModel() {
    var ID: ObservableField<String> = ObservableField("")
    var PassWord: ObservableField<String> = ObservableField("")
    var Name: ObservableField<String> = ObservableField("")

    var ID_Boolean: ObservableField<Boolean> = ObservableField(false)
    var PassWord_Boolean: ObservableField<Boolean> = ObservableField(false)
    var Name_Boolean: ObservableField<Boolean> = ObservableField(false)

    var ID_Message: ObservableField<String> = ObservableField("")
    var PassWord_Message: ObservableField<String> = ObservableField("")
    var Name_Message: ObservableField<String> = ObservableField("")

    var loginModel : MyInfo_Model? = null

    init {

//        _________________________________________________________________________________________________________________________________________________________
//        아이디 양식 체크 (중복체크 포함)

        Log.d("SignUP_ViewModel", "init ↓")
        ID.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                Log.d("SignUP_ViewModel","SignUPViewModel -> init -> ID.onPropertyChanged -> ${ID.get()} ↓")

                if (TextUtils.isEmpty(ID.get())) {
                    Log.d("SignUP_ViewModel", "ID.onPropertyChanged -> 아이디 입력이 없습니다. ↓")
                    setMessage(0,ID_Message)
                    return ID_Boolean.set(false)

                } else if (Pattern.matches("^[a-zA-Z0-9]{5,12}$", ID.get()!!)) {

                    Log.d("SignUP_ViewModel","ID.onPropertyChanged -> 아이디가 정규식이 일치합니다. 서버에 중복된 아이디가 있는지 검사를 시작. ↓")
                    ApiClient.getClient.Check_ID(ID.get().toString()).enqueue(object : Callback<Check_Model> {
                            override fun onFailure(call: Call<Check_Model>, t: Throwable) {
                                Log.d("SignUP_ViewModel", "onFailure -> $t.")
                                ID_Boolean.set(false)
                                setMessage(4,ID_Message)
                                return ID_Boolean.set(false)
                            }

                            override fun onResponse(call: Call<Check_Model>,response: Response<Check_Model>) {
                                Log.d("SignUP_ViewModel", "onResponse↓")
                                if (response.isSuccessful) {
                                    if (response.body()!!.Success) {
                                        Log.d("SignUP_ViewModel","response.body()!!.Success : true : 가입 가능한 ID ↓")
                                        Log.d("SignUP_ViewModel","response.body()!!.Message : " + response.body()!!.Message)
                                        ID_Boolean.set(true)
                                        setMessage(2,ID_Message)
                                        return ID_Boolean.set(true)
                                    } else {
                                        Log.d("SignUP_ViewModel","response.body()!!.Success : false. :  중복된 아이디")
                                        ID_Boolean.set(false)
                                        setMessage(3,ID_Message)
                                        return ID_Boolean.set(false)
                                    }
                                } else {
                                    Log.d("SignUP_ViewModel", "response.isSuccessful : false.")
                                    ID_Boolean.set(false)
                                    setMessage(0,ID_Message)
                                    return ID_Boolean.set(false)

                                }
                            }

                        })
                } else {
                    Log.d("SignUP_ViewModel", "ID.onPropertyChanged -> 아이디가 정규식과 일치하지 않습니다. ↓")
                    setMessage(1,ID_Message)
                    return ID_Boolean.set(false)
                }
            }
        })

//        _________________________________________________________________________________________________________________________________________________________
//        비밀번호 양식 체크

        PassWord.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                Log.d("SignUP_ViewModel","SignUPViewModel -> init -> PassWord.onPropertyChanged -> ${PassWord.get()!!} ↓")
                if (TextUtils.isEmpty(PassWord.get())) {
                    Log.d("SignUP_ViewModel", "PassWord.onPropertyChanged -> 비밀번호 입력이 없습니다. ↓")
                    setMessage(0,PassWord_Message)
                    return PassWord_Boolean.set(false)
                } else if (Pattern.matches("^[a-zA-Z0-9]{5,12}$", PassWord.get()!!)) {
                    Log.d("SignUP_ViewModel", "PassWord.onPropertyChanged -> 비밀번호가 정규식과 일치합니다. ↓")
                    setMessage(2,PassWord_Message)
                    return PassWord_Boolean.set(true)
                } else {
                    Log.d("SignUP_ViewModel","PassWord.onPropertyChanged -> 비밀번호가 정규식과 일치하지 않습니다. ↓")
                    setMessage(1,PassWord_Message)
                    return PassWord_Boolean.set(false)
                }
            }
        })


//        _________________________________________________________________________________________________________________________________________________________
//        이름 양식 체크

        Name.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                Log.d("SignUP_ViewModel","SignUPViewModel -> init -> Name.onPropertyChanged -> ${Name.get()} ↓")
                if (TextUtils.isEmpty(Name.get())) {
                    Log.d("SignUP_ViewModel", "Name.onPropertyChanged -> 이름 입력이 없습니다. ↓")
                    setMessage(0,Name_Message)
                    return Name_Boolean.set(false)
                } else if (Pattern.matches("^[a-zA-Z0-9가-힣]{2,10}$", Name.get()!!)) {
                    Log.d("SignUP_ViewModel", "Name.onPropertyChanged -> 이름이 정규식과 일치합니다. ↓")
                    setMessage(2,Name_Message)
                    return Name_Boolean.set(true)
                } else {
                    Log.d("SignUP_ViewModel", "Name.onPropertyChanged -> 이름이 정규식과 일치하지 않습니다. ↓")
                    setMessage(1,Name_Message)
                    return Name_Boolean.set(false)
                }
            }
        })
    }

    fun setMessage (i : Int, m : ObservableField<String>) {
        when (i) {
            0 -> m.set("")
            1 -> m.set("다시 확인해주세요.")
            2 -> m.set("사용 가능합니다.")
            3 -> m.set("사용 중인 ID 입니다.")
            4 -> m.set("관리자에게 문의해주세요.")
            5 -> m.set("가입완료.")
            else -> m.set("")
        }
    }
}