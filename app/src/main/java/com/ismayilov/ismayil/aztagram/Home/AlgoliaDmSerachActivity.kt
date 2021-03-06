package com.ismayilov.ismayil.aztagram.Home

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.google.firebase.auth.FirebaseAuth
import com.ismayilov.ismayil.aztagram.R
import kotlinx.android.synthetic.main.activity_algolia_dm_serach.*

class AlgoliaDmSerachActivity : AppCompatActivity() {

    private val ALGOLIA_APP_ID = "BJU8IZF87T"
    private val ALGOLIA_SEARCH_API_KEY = "1623ab1ff1c1658a320b57b9cfcb3bce"
    private val ALGOLIA_INDEX_NAME = "Aztagram"
    private val FIREBASE_DATABASE_URL = "https://aztagram-ada25.firebaseio.com/"
    private lateinit var mAuth: FirebaseAuth
    lateinit var searcher: Searcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_algolia_dm_serach)


        setupAlgoliaSearch()
        mAuth = FirebaseAuth.getInstance()

        ivBack.setOnClickListener {
            onBackPressed()
        }

        searchListRecyclerView.setOnItemClickListener { recyclerView, position, v ->
            val choosenUserId = searchListRecyclerView.get(position).getString("user_id")
            if (choosenUserId != mAuth.currentUser!!.uid){
                startActivity(Intent(this, ChatActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .putExtra("userID",choosenUserId))
            }else{
                Toast.makeText(this,"Bu sensen", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupAlgoliaSearch() {
        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME)
        val helper = InstantSearch(this, searcher)
        helper.search()
    }

    override fun onDestroy() {
        super.onDestroy()
        searcher.destroy()
    }
}
