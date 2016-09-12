package com.tkachuko.blog.frontend.markdown

trait Md {

  type Data = (String, String)

  private val markdown: Data = (
    """
      |# This is small demo of all supported features in this Markdown parser
      |
      |## Languages:
      |### Scala:
      |```scala
      |object Test {
      |    def main(args: Array[String]): Unit = println("Hello world")
      |}
      |```
      |### Javascript:
      |```javascript
      |alert("Hello world");
      |```
      |### Java:
      |```java
      |class Test {
      |    public static void main(String[] args){
      |        println("Hello world")
      |    }
      |}
      |```
      |### Bash:
      |```bash
      |ls -la
      |```
      |## Ignorance of the a element:
      |<a href = "_ignored_"></a>
      |## Italic, bold, italic-bold:
      |__hello__ - this is bold, ___hello___ - this is italic bold, _hello_ - this is italic
      |## This is lists:
      |
      |
      |* Item 1
      |* Item 2
      |
    """.stripMargin,
    """
      |<h1>This is small demo of all supported features in this Markdown parser</h1>
      |
      |<h2>Languages:</h2>
      |<h3>Scala:</h3>
      |<pre><code class="language-scala">
      |object Test {
      |    def main(args: Array[String]): Unit = println("Hello world")
      |}
      |</code></pre>
      |<h3>Javascript:</h3>
      |<pre><code class="language-javascript">
      |alert("Hello world");
      |</code></pre>
      |<h3>Java:</h3>
      |<pre><code class="language-java">
      |class Test {
      |    public static void main(String[] args){
      |        println("Hello world")
      |    }
      |}
      |</code></pre>
      |<h3>Bash:</h3>
      |<pre><code class="language-bash">
      |ls -la
      |</code></pre>
      |<h2>Ignorance of the a element:</h2>
      |<a href = "_ignored_"></a>
      |<h2>Italic, bold, italic-bold:</h2>
      |<b>hello</b> - this is bold, <b><i>hello</i></b> - this is italic bold, <i>hello</i> - this is italic
      |<h2>This is lists:</h2>
      |
      |<ul>
      |<li>Item 1</li>
      |<li>Item 2</li>
      |</ul>
    """.stripMargin
    )

  val small: Data = markdown

  val medium: Data = *(small)(20)

  val large = *(small)(50)

  val `markdown post` = "# Back to school: 1D ladder game\n\nLet's play! Imagine you are lifting up a ladder of given finite size where at each step you are given a number which indicates how many maximum steps you can go up. You win only when you are able to reach the end of a ladder. Let's define what is known:\n\n* Size of the ladder _N_\n* _a<sub>i</sub>_ - number of steps that you can make at step _i_\n\n### Task #1: define if you can win in a given game situation:\n\nLet's introduce some variables:\n\n* _furthermostSoFar_ - number of steps it is possible to make to our current best knowledge\n* _board_ - array(list) of values which indicate how many steps allowed at step (index) _i_\n\nThen we can express _furthermostSoFar_ in terms of variables mentioned above:\n\n```java\nfurthermostSoFar = Math.max(\n\tfurthermostSoFar,\n\tboard.get(i) + i\n)\n```\nWe express the following: on each step our maximum value either does not change or it changes because we can proceed further standing at position _i_ and being allowed to go _board.get(i)_ steps up. All other restrictions on our iteration (or going up the ladder) should be quite obvious which leads us to the following implementation:\n\n```java\n/**\n * Indicated if win can be achieved in a board game where each number on the board indicates how you can go from\n * that point. Win can be achieved if you can get to the end of the board.\n *\n * @param board board representation\n * @return if win can be achieved\n */\npublic static boolean canReachEnd(List<Integer> board) {\n    int lastIndex = board.size() - 1;\n    int furthermostSoFar = 0;\n    for (int i = 0; i <= furthermostSoFar && furthermostSoFar < lastIndex; i++) {\n        furthermostSoFar = Math.max(furthermostSoFar, board.get(i) + i);\n    }\n    return furthermostSoFar >= lastIndex;\n}\n```\nAs you can see we explicitly restrict ourselves to stay inside the ladder boundaries with the condition:\n\n```java\ni <= furthermostSoFar &&       // We walk only the steps which we can achieve\n  furthermostSoFar < lastIndex // We stay inside of the ladder\n```\nBelow find test cases for the first task:\n\n```java\n@Test\n@Parameters\npublic void shouldDefineIfEndCanBeReached(List<Integer> board, boolean canReachEnd) {\n    assertEquals(canReachEnd, canReachEnd(board));\n}\n\npublic Object parametersForShouldDefineIfEndCanBeReached() {\n    return new Object[]{\n            new Object[]{asList(1, 1, 1), true},\n            new Object[]{asList(3, 3, 0, 0, 0, 0, 1), false},\n            new Object[]{asList(3, 2, 1, 1, 1, 0), true},\n            new Object[]{asList(3, 2, 1, 1, 0, 0), false}\n    };\n}\n```\n\n### Task #2: calculate minimum number of steps you need to do to win:\n\nActually to answer this question you need to answer more interesting one: when do you make an _optimal_ move in previous iteration? It turns out that _optimal_ move will be the _longest_ one and that one you define exactly when maximum changes. So With a little modification of our previous solution we get the following method:\n\n```java\n/**\n * Modification of canReachEnd to return minimum number of steps required to reach the end of board.\n *\n * @param board board representation\n * @return minimum number of steps required to win\n */\npublic static int minNumberOfStepsToReachEnd(List<Integer> board) {\n    if (!canReachEnd(board)) return -1; // check that we can win\n    int lastIndex = board.size() - 1;\n    int furthermostSoFar = 0;\n    int steps = 0;\n    for (int i = 0; i <= furthermostSoFar && furthermostSoFar < lastIndex; i++) {\n        int candidate = board.get(i) + i;\n        if (candidate > furthermostSoFar) { // found better move\n            steps++;                        // making that move\n            furthermostSoFar = candidate;\n        }\n    }\n    return steps;\n}\n```\nAnd test cases to this subtask:\n\n```java\n@Test\n@Parameters\npublic void shouldFindMinNumberOfStepsRequiredToWin(List<Integer> board, int minSteps) {\n    assertEquals(minSteps, minNumberOfStepsToReachEnd(board));\n}\n\npublic Object parametersForShouldFindMinNumberOfStepsRequiredToWin() {\n    return new Object[]{\n            new Object[]{asList(1, 1, 0), 2},\n            new Object[]{asList(3, 2, 1, 1, 1, 0), 3},\n            new Object[]{asList(3, 2, 1, 3, 1, 1), 2},\n            new Object[]{asList(9, 2, 1, 3, 1, 1), 1},\n            new Object[]{asList(1, 2, 9, 1, 1, 1), 3}\n    };\n}\n```\nHappy coding!"

  private def *(data: Data)(value: Int): Data = {
    def populate(s: String): String = (0 to value)./:(s) { case (acc, _) => acc + s }
    (populate(data._1), populate(data._2))
  }
}
