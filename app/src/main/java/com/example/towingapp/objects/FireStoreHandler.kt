package com.example.towingapp.objects

import android.util.Log
import com.example.towingapp.Utils.SharedPref
import com.example.towingapp.fragments.MapFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class FireStoreHandler {


    private val db = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth
    private var registration: ListenerRegistration? = null

    companion object {
        private const val TOWING_USERS = "TowingUsers"
         private const val REQUEST_TOWING = "RequestTowing"
        private const val TOWING = "Towing"
        private const val USERS = "users"
    }

    interface UserInfoCallBack {
        fun isSucceeded()
        fun isFailed(cause: Throwable?)
    }

    interface UserRequestDetail {
     fun  getTowingDetailCallBack(detail:UserDetailForTowing)
    }

    interface TowingDetailCallBack {
        fun  towingDetailCallBack(towingDetail:User)
    }


    fun saveUser(user: User, userInfoCallBack: UserInfoCallBack) {

        Log.d("FDsfdsdfsfd" , "DFSsfdsfdsfd" + user)
        user.id?.let {
            db.collection(TOWING_USERS).document(it)
                .set(user)
                .addOnSuccessListener {
                    userInfoCallBack.isSucceeded()
                }
                .addOnFailureListener { e ->
                    userInfoCallBack.isFailed(e.cause)

                }
        }


    }

    fun listenerUserTowingRequest (userRequestDetail :UserRequestDetail) {

//       val whenDestroyAppStr = SharedPref.getInstance().getString(MapFragment.DESTROY_APP , "");
//        var whenDestroyApp  = 0.0
//        if (whenDestroyAppStr.isNotEmpty()) {
//           whenDestroyApp = whenDestroyAppStr.toDouble()
//        }

        val timeMill = System.currentTimeMillis()

        Log.d("1111111111111", "Listen failed.")
        val docRef = db.collection(REQUEST_TOWING)
         registration  = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {

                    Log.d("222222222222222", "Listen failed.")
                    val userDetailForTowing = UserDetailForTowing()
                    for (snap in snapshot.documentChanges) {

                        val userDetail = snap.document.toObject(UserDetailForTowing::class.java);
                        val timeWasTowingRequest = userDetail.requestId?.toLong() ?:0.0.toLong()
                            if(timeMill < timeWasTowingRequest) {

                                userDetailForTowing.name = userDetail.name
                                userDetailForTowing.imageUri = userDetail.imageUri
                                userDetailForTowing.latitude = userDetail.latitude
                                userDetailForTowing.longitude = userDetail.longitude
                                userDetailForTowing.Userid = userDetail.Userid
                                userDetailForTowing.requestId = userDetail.requestId
                                userRequestDetail.getTowingDetailCallBack((userDetailForTowing))

                            }
                    }




            } else {
             //   Log.d(TAG, "Current data: null")
            }
        }



    }
    fun shoutDownListenerUserTowingRequest() {
        registration?.remove()
    }

    fun getTowingProfile(towingDetailCallBack:TowingDetailCallBack ) {

        auth.currentUser?.let { db.collection(TOWING_USERS).document(it.uid)
            .get()
            .addOnSuccessListener { documentSnapshot->
                val towingDetail = documentSnapshot.toObject<User>()

                if(towingDetail != null) {
                    towingDetailCallBack.towingDetailCallBack(towingDetail)
                }

            }


        }
    }

    fun sendDetailTowingToUser(towingProfileWithLocation: UserDetailForTowing) {

        Log.d("gdfgfd" , "DFSsfdsfdsfd" )
        towingProfileWithLocation.Userid?.let {
            towingProfileWithLocation.requestId?.let { it1 ->
                db.collection(USERS).document(it).collection(TOWING).document(it1)
                    .set(towingProfileWithLocation)
                    .addOnSuccessListener {
                        // userInfoCallBack.isSucceeded()
                    }
                    .addOnFailureListener { e ->
                        //  userInfoCallBack.isFailed(e.cause)

                    }
            }
        }

    }

    fun deleteTowingFromUser(towingDetailProfile: UserDetailForTowing?) {
       val userIDoc = towingDetailProfile?.Userid ?: "null"
        val requestId = towingDetailProfile?.requestId ?: "null"
        val productIdRef: DocumentReference =
            db.collection(USERS).document(userIDoc).collection(TOWING).document(requestId)
              productIdRef.delete().addOnSuccessListener {

              }



    }


}