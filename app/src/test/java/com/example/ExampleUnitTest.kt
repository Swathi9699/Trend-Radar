package com.example

import com.example.domain.ViralityEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun viralityEngine_analyzesHooksAccurately() {
    val result = ViralityEngine.analyzeText("Wait until the end to see this secret recipe...")
    assertTrue(result.score > 60)
    assertTrue(result.retentionEstimatePercent > 50)
    assertTrue(result.viralVariations.isNotEmpty())
  }

  @Test
  fun viralityEngine_generatesHooksForNiche() {
    val hooks = ViralityEngine.generateHooks("Food & Treats", "Curiosity Gap")
    assertEquals(3, hooks.size)
    assertTrue(hooks.all { it.visualActionCue.isNotBlank() })
  }
}
