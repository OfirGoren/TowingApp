package com.example.towingapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.towingapp.R
import com.example.towingapp.Utils.WindowUtils
import com.example.towingapp.activitiy.TowingActivity
import com.example.towingapp.databinding.FragmentLoginBinding
import com.example.towingapp.objects.AuthSignUp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private lateinit var authSignUp: AuthSignUp
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        initValues()

        binding.loginBtn.setOnClickListener(logInClick)



        return binding.root

    }

    private fun initValues() {
        authSignUp = AuthSignUp()
    }


    private val logInClick = View.OnClickListener { view ->
        WindowUtils.closeKeyBoardView(requireActivity(), view)
        logInClickWithEmailHandler()


    }


    private fun logInClickWithEmailHandler() {
        val email = binding.logInEmail.editText?.text.toString()
        val password = binding.logInPassword.editText?.text.toString()
        if (email.isEmpty() || password.isEmpty()) {

            binding.logInTXTErrorMsg.text = getString(R.string.log_is_failed_try_again)
            setVisibilityProgressAndErrorText(View.INVISIBLE, View.VISIBLE)
            //exit from Method
            return
        }
        // handle progress bar and error msg display
        setVisibilityProgressAndErrorText(View.VISIBLE, View.INVISIBLE)
        // log In to account
        authSignUp.logInUser(requireActivity(), email, password, logInCallBack)

    }

    private fun setVisibilityProgressAndErrorText(
        visibilityProgressBar: Int,
        visibilityErrorMsg: Int
    ) {
        binding.loginPRB.visibility = visibilityProgressBar
        binding.logInTXTErrorMsg.visibility = visibilityErrorMsg

    }

    // callback On login mode
    private val logInCallBack = object : AuthSignUp.LogInCallBack {
        //when we log in to user account is succeeded
        override fun isSucceeded() {
            //check if the email was verified
            authSignUp.isEmailVerified(object : AuthSignUp.LogInCallBack {
                // when email was verified
                override fun isSucceeded() {

                    // enter the user
                    openTowingActivity()
                }

                //when email wasn't verified
                override fun isFailed() {
                    binding.logInTXTErrorMsg.text = getString(R.string.needToVerify)
                    setVisibilityProgressAndErrorText(View.INVISIBLE, View.VISIBLE)

                }
            })
        }

        // when log in failed
        override fun isFailed() {
            // show msg to user
            binding.logInTXTErrorMsg.text = getString(R.string.log_is_failed_try_again)
            // show error msg to user , and close the progressBar
            setVisibilityProgressAndErrorText(View.INVISIBLE, View.VISIBLE)


        }

    }

    private fun openTowingActivity() {
        val intent = Intent(activity, TowingActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}