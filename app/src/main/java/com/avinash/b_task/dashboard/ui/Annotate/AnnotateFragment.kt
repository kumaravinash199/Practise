package com.avi


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.avinash.b_task.dashboard.ui.Annotate.AnnotateViewModel
import com.avinash.b_task.databinding.FragmentHomeBinding


class AnnotateFragment : Fragment(),View.OnTouchListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        const val ARG_NAME = "data"


        fun newInstance(name: String): AnnotateFragment {
            val fragment = AnnotateFragment()

            val bundle = Bundle().apply {
                putString(ARG_NAME, name)
            }

            fragment.arguments = bundle

            return fragment
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val annotateViewModel =
            ViewModelProvider(this).get(AnnotateViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
            var data:String?=arguments?.getString(ARG_NAME)
            textView.text = data
            if(textView.text.isNotEmpty()) {
                textView.visibility = View.VISIBLE
            }else{
                textView.visibility = View.VISIBLE
            }

            textView.setOnTouchListener(this)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    var dX = 0f
    var dY = 0f
    var lastAction = 0
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        when (event!!.getActionMasked()) {
            MotionEvent.ACTION_DOWN -> {
                dX = view!!.x - event.getRawX()
                dY = view!!.y - event.getRawY()
                lastAction = MotionEvent.ACTION_DOWN
            }
            MotionEvent.ACTION_MOVE -> {
                view!!.y = event.getRawY() + dY
                view!!.x = event.getRawX() + dX
                lastAction = MotionEvent.ACTION_MOVE
            }
            MotionEvent.ACTION_UP -> if (lastAction === MotionEvent.ACTION_DOWN)

            else -> return false
        }
        return true
    }


}