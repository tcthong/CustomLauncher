package utc.thong.customlauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

private const val TAG = "MainActivity"
private const val SIZE_RECYCLER_VIEW = 4

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, SIZE_RECYCLER_VIEW)

        setupAdapter()
    }

    private fun setupAdapter() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = packageManager.queryIntentActivities(intent, 0)
        activities.sortWith { a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                    a.loadLabel(packageManager).toString(),
                    b.loadLabel(packageManager).toString()
            )
        }

        recyclerView.adapter = LauncherAdapter(activities)
    }

    private class LauncherAdapter(val activities: List<ResolveInfo>) : RecyclerView.Adapter<LauncherViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LauncherViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)

            val view = inflater.inflate(R.layout.item_layout, parent, false)
            return LauncherViewHolder(view)
        }

        override fun onBindViewHolder(holder: LauncherViewHolder, position: Int) {
            holder.bind(activities.get(position))
        }

        override fun getItemCount() = activities.size
    }

    private class LauncherViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val iconAppImg: ImageView = view.findViewById(R.id.icon_app_img)
        private val nameAppTv: TextView = view.findViewById(R.id.name_app_tv)
        private lateinit var resolveInfo: ResolveInfo

        init {
            view.setOnClickListener(this)
        }

        fun bind(resolveInfo: ResolveInfo) {
            val packageManager: PackageManager = itemView.context.packageManager
            iconAppImg.setImageDrawable(resolveInfo.loadIcon(packageManager))
            nameAppTv.text = resolveInfo.loadLabel(packageManager)
            this.resolveInfo = resolveInfo
        }

        override fun onClick(v: View) {
            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            v.context.startActivity(intent)
        }
    }
}