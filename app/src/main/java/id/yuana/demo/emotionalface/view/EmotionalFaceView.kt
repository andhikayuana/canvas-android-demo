package id.yuana.demo.emotionalface.view

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import id.yuana.demo.emotionalface.R
import kotlin.math.min

class EmotionalFaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_FACE_COLOR = Color.YELLOW
        private const val DEFAULT_EYES_COLOR = Color.BLACK
        private const val DEFAULT_MOUTH_COLOR = Color.BLACK
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
        private const val DEFAULT_BORDER_WIDTH = 4.0f

        const val HAPPY = 0L
        const val SAD = 1L
    }

    private var faceColor = DEFAULT_FACE_COLOR
    private var eyesColor = DEFAULT_EYES_COLOR
    private var mouthColor = DEFAULT_MOUTH_COLOR
    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH

    private val paint = Paint()
    private val mouthPath = Path()
    private var size = 0

    var happinessState = HAPPY
        set(state) {
            field = state
            invalidate()
        }

    init {
        paint.isAntiAlias = true
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        //obtain from attrs.xml
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.EmotionalFaceView,
            0, 0
        )

        happinessState =
            typedArray.getInt(R.styleable.EmotionalFaceView_state, HAPPY.toInt()).toLong()
        faceColor = typedArray.getColor(R.styleable.EmotionalFaceView_faceColor, DEFAULT_FACE_COLOR)
        eyesColor = typedArray.getColor(R.styleable.EmotionalFaceView_eyesColor, DEFAULT_EYES_COLOR)
        mouthColor =
            typedArray.getColor(R.styleable.EmotionalFaceView_mouthColor, DEFAULT_MOUTH_COLOR)
        borderColor = typedArray.getColor(
            R.styleable.EmotionalFaceView_borderColor,
            DEFAULT_BORDER_COLOR
        )
        borderWidth = typedArray.getDimension(
            R.styleable.EmotionalFaceView_borderWidth,
            DEFAULT_BORDER_WIDTH
        )

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawFaceBackground(canvas)
        drawEyes(canvas)
        drawMouth(canvas)
    }

    private fun drawFaceBackground(canvas: Canvas) {
        paint.apply {
            color = faceColor
            style = Paint.Style.FILL
        }
        val radius = size / 2f
        canvas.drawCircle(size / 2f, size / 2f, radius, paint)

        paint.apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
        }

        canvas.drawCircle(size / 2f, size / 2f, radius - borderWidth / 2f, paint)
    }

    private fun drawEyes(canvas: Canvas) {
        paint.apply {
            color = eyesColor
            style = Paint.Style.FILL
        }
        val leftEyeRect = RectF(size * 0.32f, size * 0.23f, size * 0.43f, size * 0.50f)
        canvas.drawOval(leftEyeRect, paint)

        val rightEyeRect = RectF(size * 0.57f, size * 0.23f, size * 0.68f, size * 0.50f)
        canvas.drawOval(rightEyeRect, paint)
    }

    private fun drawMouth(canvas: Canvas) {
        mouthPath.reset()
        mouthPath.apply {
            moveTo(size * 0.22f, size * 0.7f)

            if (isHappy()) {
                // Happy mouth path
                mouthPath.quadTo(size * 0.5f, size * 0.80f, size * 0.78f, size * 0.7f)
                mouthPath.quadTo(size * 0.5f, size * 0.90f, size * 0.22f, size * 0.7f)
            } else {
                // Sad mouth path
                mouthPath.quadTo(size * 0.5f, size * 0.50f, size * 0.78f, size * 0.7f)
                mouthPath.quadTo(size * 0.5f, size * 0.60f, size * 0.22f, size * 0.7f)
            }
        }

        paint.apply {
            color = mouthColor
            style = Paint.Style.FILL
        }

        canvas.drawPath(mouthPath, paint)

    }

    private fun isHappy() = happinessState == HAPPY

    //    Responsive View
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        size = min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    //save state, like rotate screen
    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putLong("happinessState", happinessState)
        bundle.putParcelable("superState", super.onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var viewState = state
        if (viewState is Bundle) {
            happinessState = viewState.getLong("happinessState", HAPPY)
            viewState = viewState.getParcelable("superState")!!
        }
        super.onRestoreInstanceState(viewState)
    }

}