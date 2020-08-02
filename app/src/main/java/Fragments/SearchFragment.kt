package Fragments

import Adapter.UserAdapter
import Model.User
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.rgbat.zeechat.R
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.util.*
import android.text.TextWatcher as TextWatcher

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {
    private var recyclerView: RecyclerView?= null
    private var userAdapter:UserAdapter? = null
    private var mUser:MutableList<User>?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it,mUser as ArrayList<User>,true) }
        recyclerView?.adapter= userAdapter

      view.search_edit_text.addTextChangedListener(object : TextWatcher {


          override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

          }

          override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

              if (view.search_edit_text.text.toString() == ""){



              }
              else{

                  recyclerView?.visibility = View.VISIBLE


                  retrieveUsers()
                  searchUser(p0.toString().toLowerCase())
              }
          }
          override fun afterTextChanged(p0: Editable?) {

          }



      })

//        view.search_edit_text.addTextChangedListener { object :TextWatcher{
//
//
//            override fun beforeTextChanged(m: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//                if (view.search_edit_text.text.toString() == ""){
//
//
//
//                }
//                else{
//
//                    recyclerView?.visibility = View.VISIBLE
//
//
//                    retrieveUsers()
//                    searchUser(s.toString().toLowerCase())
//                }
//            }
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//        } }


        return view
    }


    private fun searchUser(input: String) {


        val query = FirebaseDatabase.getInstance().getReference().child("Users")
            .orderByChild("fullname")

            .startAt(input).endAt(input + "\uf8ff")


        query.addValueEventListener(object :ValueEventListener{


            override fun onDataChange(datasnapshot: DataSnapshot) {

                mUser?.clear()


                    for (snapshot in datasnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {

                            mUser?.add(user)
                        }
                    }
                userAdapter?.notifyDataSetChanged()



                }
            override fun onCancelled(error: DatabaseError) {

            }

        })



    }





    private fun retrieveUsers() {


        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")

        userRef.addValueEventListener(object :ValueEventListener{



            override fun onDataChange(datasnapshot: DataSnapshot) {

               if (view?.search_edit_text?.text.toString()==""){


                   mUser?.clear()

                   for (snapshot in datasnapshot.children){
                       val user = snapshot.getValue(User::class.java)
                       if (user != null){

                           mUser?.add(user)
                       }
                   }
                   userAdapter?.notifyDataSetChanged()


               }

            }
            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

}
