package com.example.towingapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.towingapp.objects.AuthSignUp
import com.example.towingapp.objects.FireStoreHandler
import com.example.towingapp.R
import com.example.towingapp.objects.User
import com.example.towingapp.Utils.ToastyUtils
import com.example.towingapp.activitiy.PermissionActivity
import com.example.towingapp.activitiy.TowingActivity
import com.example.towingapp.databinding.FragmentSignUpBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpFragment : Fragment() {


    private lateinit var binding: FragmentSignUpBinding
    private lateinit var authSignUp: AuthSignUp
    val auth: FirebaseAuth = Firebase.auth
    private var toastyMsg: ToastyUtils? = null
    private lateinit var fireStoreHandler: FireStoreHandler
    private var newUser = User()

    companion object {
        const val REGISTER = "Register"
        const val VERIFY = "Verify"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        initValues()
        initListeners()

        return binding.root
    }

    private fun initValues() {
        authSignUp = AuthSignUp()
        toastyMsg = ToastyUtils(requireContext().applicationContext)
        fireStoreHandler = FireStoreHandler()
    }

    private fun initListeners() {
        binding.signUpBtn.setOnClickListener(registerBtn)
    }


    private val registerBtn = View.OnClickListener { view ->
        // show error to user (when he doesn't filled all the fields)
        errorsHandler()
        val btnText = binding.signUpBtn.text.toString()
        visibilityGoneBtn(View.GONE)
        // when all fields inside (by the user)
        if (!isErrorsMsgPerform()) {
            binding.signUpPRB.visibility = View.VISIBLE
            if (btnText == REGISTER) {

                signUpTowing()

            } else if (btnText == VERIFY) {

                handlerEmailVerification()


            }
        } else {
            binding.signUpPRB.visibility = View.GONE
            visibilityGoneBtn(View.VISIBLE)
        }

    }


    private fun handlerEmailVerification() {
        authSignUp.isEmailVerified(object : AuthSignUp.LogInCallBack {
            override fun isSucceeded() {
                openMapActivity()

            }

            override fun isFailed() {
                binding.signUpPRB.visibility = View.INVISIBLE
                visibilityGoneBtn(View.VISIBLE)
                toastyMsg?.toastyInfo("You Didn't Verify Your Account")
            }

        })
    }

    private fun openMapActivity() {
        val intent = Intent(activity, PermissionActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun signUpTowing() {
        val email = binding.signUpEmail.editText?.text.toString()
        val password = binding.signUpEDTPassword.editText?.text.toString()
        // create new account
        authSignUp.registerNewUser(
            requireActivity(), email, password, registerNewUserCallBack
        )
    }


    private fun isErrorsMsgPerform(): Boolean {

        return when {
            binding.signUpFullName.isErrorEnabled -> {
                true
            }
            binding.signUpEmail.isErrorEnabled -> {
                true
            }
            binding.signUpEDTPassword.isErrorEnabled -> {
                true
            }

            else -> false
        }
    }


    private fun visibilityGoneBtn(visibilityBtn: Int) {
        binding.signUpBtn.visibility = visibilityBtn

    }

    private fun errorMsgHandlerPassword(
        signUpEDTPassword: TextInputLayout,
        string: String
    ): Boolean {
        if (!errorMsgHandler(signUpEDTPassword, string)) {
            if (signUpEDTPassword.editText?.text.toString().length < 6) {
                signUpEDTPassword.isErrorEnabled = true
                signUpEDTPassword.error = "Must have at least a \n6-character password"
                return true
            }
        }
        return false
    }


    private fun errorsHandler() {
        errorMsgHandler(binding.signUpFullName, getString(R.string.require_name))
        errorMsgHandler(binding.signUpEmail, getString(R.string.email_required))
        errorMsgHandlerPassword(binding.signUpEDTPassword, getString(R.string.password_required))
    }


    private fun errorMsgHandler(textInput: TextInputLayout, str: String): Boolean {
        if (textInput.editText?.text?.isEmpty() == true) {
            textInput.isErrorEnabled = true
            textInput.error = str
            return true
        } else {
            textInput.isErrorEnabled = false
        }
        return false
    }

    private val registerNewUserCallBack = object : AuthSignUp.RegisterNewUserCallBack {

        override fun isSucceeded(isNewUser: Boolean?) {

            visibilityGoneBtn(View.VISIBLE)
            // succeed sign up
            if (isNewUser == true) {
                // openMainActivity()
                newUser = getUserDetails()

                fireStoreHandler.saveUser(newUser, object : FireStoreHandler.UserInfoCallBack {
                    override fun isSucceeded() {}
                    override fun isFailed(cause: Throwable?) {}
                })

                binding.signUpBtn.text = VERIFY
                binding.signUpPRB.visibility = View.INVISIBLE
                toastyMsg?.toastyInfo("Please verify Your Email id")

                //save user in FireStore
                //  fireStoreHandler.saveUser(getUserDetails())


                // there is email Exists in auth
            } else if (isNewUser == false) {
                // show msg to user
                // updateEmailErrorMsg()

                // showMsgDialogToUser()
                // stop progress bar

                binding.signUpPRB.visibility = View.INVISIBLE
            }
        }

        override fun isFailed() {
            TODO("Not yet implemented")
        }

    }

    private fun getUserDetails(): User {
        val name = binding.signUpFullName.editText?.text.toString()
        val email = binding.signUpEmail.editText?.text.toString()
        val id = auth.currentUser?.uid
        return User(name, email, "", id)


    }
}


