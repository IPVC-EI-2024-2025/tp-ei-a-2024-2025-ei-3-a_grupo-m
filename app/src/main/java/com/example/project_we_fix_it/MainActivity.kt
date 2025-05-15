package com.example.project_we_fix_it

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var breakdownAdapter: BreakdownAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.breakdownsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create sample data
        val breakdowns = createSampleBreakdowns()

        // Set up the adapter
        breakdownAdapter = BreakdownAdapter(breakdowns)
        recyclerView.adapter = breakdownAdapter

        // Set up bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.navigation_home

        // Set up chat FAB
        val chatFab = findViewById<FloatingActionButton>(R.id.chatFab)
        chatFab.setOnClickListener {
            // Handle chat button click
        }

        // Set up menu and settings click listeners
        val menuButton = findViewById<ImageView>(R.id.menuButton)
        menuButton.setOnClickListener {
            // Handle menu button click
        }

        val settingsButton = findViewById<ImageView>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            // Handle settings button click
        }
    }

    private fun createSampleBreakdowns(): List<Breakdown> {
        return listOf(
            Breakdown("List item", "Supporting line text lorem ipsum dolor sit amet, consectetur."),
            Breakdown("List item", "Supporting line text lorem ipsum dolor sit amet, consectetur."),
            Breakdown("List item", "Supporting line text lorem ipsum dolor sit amet, consectetur."),
            Breakdown("List item", "Supporting line text lorem ipsum dolor sit amet, consectetur."),
            Breakdown("List item", "Supporting line text lorem ipsum dolor sit amet, consectetur.")
        )
    }
}

// Data class for breakdown items
data class Breakdown(val title: String, val description: String)

// Adapter for the RecyclerView
class BreakdownAdapter(private val breakdowns: List<Breakdown>) :
    RecyclerView.Adapter<BreakdownAdapter.BreakdownViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreakdownViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_breakdown, parent, false)
        return BreakdownViewHolder(view)
    }

    override fun onBindViewHolder(holder: BreakdownViewHolder, position: Int) {
        val breakdown = breakdowns[position]
        holder.titleTextView.text = breakdown.title
        holder.descriptionTextView.text = breakdown.description

        // Handle item clicks
        holder.itemView.setOnClickListener {
            // Handle breakdown item click
        }

        // Handle bookmark clicks
        holder.bookmarkIcon.setOnClickListener {
            // Handle bookmark click
        }
    }

    override fun getItemCount() = breakdowns.size

    class BreakdownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.listItemTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.listItemDescription)
        val bookmarkIcon: ImageView = itemView.findViewById(R.id.bookmarkIcon)
    }
}