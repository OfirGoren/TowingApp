package com.example.towingapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.towingapp.R
import com.example.towingapp.activitiy.TowingActivity
import com.example.towingapp.databinding.FragmentWelcomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        clickListeners()

        isUserStillLogIn()
        return binding.root


    }

    private fun isUserStillLogIn() {
        val user = Firebase.auth.currentUser

        if (user != null) {
            openTowingActivity()
        } else {
            // No user is signed in
        }
    }

    private fun openTowingActivity() {
        val intent = Intent(activity, TowingActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun clickListeners() {
        binding.welcomeBtnSignUp.setOnClickListener(signUpClick)
        binding.welcomeBTNLogIn.setOnClickListener(logInClick)
    }


    private val signUpClick: View.OnClickListener = View.OnClickListener { view ->
        view.findNavController().navigate(R.id.action_welcomeFragment_to_signUpFragment)

    }

    private val logInClick: View.OnClickListener = View.OnClickListener { view ->
        view.findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)

    }


}