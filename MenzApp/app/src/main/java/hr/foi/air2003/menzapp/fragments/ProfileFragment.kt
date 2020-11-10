package hr.foi.air2003.menzapp.fragments

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.transition.Explode
import androidx.transition.TransitionManager
import hr.foi.air2003.menzapp.R
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {
   override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

       view.expandablePosts.visibility = View.GONE
       view.expandableFeedbacks.visibility = View.GONE

       view.cvMyPosts.setOnClickListener{
           if(view.expandablePosts.visibility == View.GONE){
               TransitionManager.beginDelayedTransition(view.cvMyPosts, Explode())
               view.expandablePosts.visibility = View.VISIBLE
           } else{
               TransitionManager.beginDelayedTransition(view.cvMyPosts, Explode())
               view.expandablePosts.visibility = View.GONE
           }

           // Edit post
           btnEditPost.setOnClickListener{
               val popupEditPost = Dialog(view.context)
               popupEditPost.setContentView(layoutInflater.inflate(R.layout.dialog_edit_post,null))

               popupEditPost.show()


           }

       }


       view.cvFeedback.setOnClickListener {
           if(view.expandableFeedbacks.visibility == View.GONE){
               TransitionManager.beginDelayedTransition(view.cvFeedback, Explode())
               view.expandableFeedbacks.visibility = View.VISIBLE
           } else{
               TransitionManager.beginDelayedTransition(view.cvFeedback, Explode())
               view.expandableFeedbacks.visibility = View.GONE
           }
       }

       return view
    }
}