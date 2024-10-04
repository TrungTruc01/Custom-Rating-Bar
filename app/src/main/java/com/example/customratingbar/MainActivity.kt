package com.example.customratingbar
import android.view.LayoutInflater

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.util.Log // Thêm import này
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var customRatingBar: CustomRatingBar
    private lateinit var ratingHistory: MutableList<RatingRecord>
    private lateinit var customAdapter: ArrayAdapter<String>
    private lateinit var deleteSelectedButton: Button
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo các view
        customRatingBar = findViewById(R.id.customRatingBar)
        val ratingText = findViewById<TextView>(R.id.ratingText)
        val filterSpinner = findViewById<Spinner>(R.id.filterSpinner)
        val rateButton = findViewById<Button>(R.id.rateButton)
        listView = findViewById(R.id.ratingListView)

        // Khai báo nút xóa
        deleteSelectedButton = findViewById(R.id.deleteSelectedButton)

        ratingHistory = mutableListOf()

        customAdapter = object : ArrayAdapter<String>(this, R.layout.rating_list_item, mutableListOf()) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.rating_list_item, parent, false)

                // Lấy TextView từ layout
                val ratingTextView = view.findViewById<TextView>(R.id.ratingTextView)
                ratingTextView.text = getItem(position)

                val deleteButton = view.findViewById<Button>(R.id.deleteButton)

                // Sự kiện khi nhấn nút xóa
                deleteButton.setOnClickListener {
                    if (position >= 0 && position < ratingHistory.size) {
                        ratingHistory.removeAt(position) // Xóa đánh giá
                        updateRatingHistory() // Cập nhật lại danh sách
                    }
                }

                return view
            }
        }

        listView.adapter = customAdapter

        val filters = arrayOf("Tất cả", "Tích cực", "Tiêu cực", "5 sao", "4 sao", "3 sao", "2 sao", "1 sao")
        filterSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, filters)

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterRatingHistory(filters[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Cập nhật TextView khi rating thay đổi
        customRatingBar.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                val rating = customRatingBar.getRating()
                ratingText.text = "Đánh giá: $rating sao"
            }
            false
        }

        // Sự kiện khi nhấn nút đánh giá
        rateButton.setOnClickListener {
            val rating = customRatingBar.getRating()
            if (rating > 0) {
                val newRating = RatingRecord(rating, System.currentTimeMillis())
                ratingHistory.add(newRating)
                updateRatingHistory() // Cập nhật danh sách
                ratingText.text = "Đánh giá: $rating sao"
            } else {
                Toast.makeText(this, "Vui lòng chọn số sao trước khi đánh giá!", Toast.LENGTH_SHORT).show()
            }
        }

        // Thêm sự kiện cho nút xóa đã chọn
        deleteSelectedButton.setOnClickListener {
            deleteSelectedRatings()
        }
    }

    private fun updateRatingHistory() {
        // Định dạng thời gian
        val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
        val ratingsDisplay = ratingHistory.mapIndexed { _, ratingRecord ->
            val formattedTime = dateFormat.format(Date(ratingRecord.time))
            "Đánh giá: ${ratingRecord.rating} sao lúc $formattedTime"
        }

        // In ra log để kiểm tra nội dung
        Log.d("RatingHistory", ratingsDisplay.toString()) // Sử dụng Log để in ra danh sách

        customAdapter.clear() // Xóa dữ liệu cũ
        customAdapter.addAll(ratingsDisplay) // Thêm dữ liệu mới
        customAdapter.notifyDataSetChanged() // Cập nhật danh sách
    }

    private fun filterRatingHistory(filter: String) {
        val filteredRatings = when (filter) {
            "Tích cực" -> ratingHistory.filter { it.rating >= 4 }
            "Tiêu cực" -> ratingHistory.filter { it.rating < 3 }
            "5 sao" -> ratingHistory.filter { it.rating == 5f }
            "4 sao" -> ratingHistory.filter { it.rating == 4f }
            "3 sao" -> ratingHistory.filter { it.rating == 3f }
            "2 sao" -> ratingHistory.filter { it.rating == 2f }
            "1 sao" -> ratingHistory.filter { it.rating == 1f }
            else -> ratingHistory
        }

        // Định dạng thời gian
        val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
        val ratingsDisplay = filteredRatings.map {
            val formattedTime = dateFormat.format(Date(it.time))
            "Đánh giá: ${it.rating} sao vào lúc $formattedTime"
        }

        customAdapter.clear()
        customAdapter.addAll(ratingsDisplay)
        customAdapter.notifyDataSetChanged()
    }

    private fun deleteSelectedRatings() {
        // Logic để xóa các đánh giá đã chọn
        // Bạn cần thêm logic để xác định đánh giá nào cần xóa
    }

    data class RatingRecord(val rating: Float, val time: Long)
}
