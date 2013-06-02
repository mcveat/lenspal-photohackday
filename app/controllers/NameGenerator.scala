package controllers

import scala.util.Random

/**
 * User: mcveat
 */
object NameGenerator {
  def get = List(
      adjectives(Random.nextInt(adjectives.size)),
      nouns(Random.nextInt(nouns.size))
    ).mkString(" ")
  val adjectives = List(
    "adorable", "beautiful", "clean", "drab", "elegant", "fancy", "glamorous", "handsome", "long",
    "magnificent", "old-fashioned", "plain", "quaint", "sparkling", "ugliest", "unsightly",
    "red", "orange", "yellow", "green", "blue", "purple", "gray", "black", "white",
    "alive", "better", "careful", "clever", "dead", "easy", "famous", "gifted", "helpful", "important",
    "inexpensive", "mushy", "odd", "powerful", "rich", "shy", "tender", "uninterested", "vast",
    "wrong", "angry", "bewildered", "clumsy", "defeated", "embarrassed", "fierce", "grumpy",
    "helpless", "itchy", "jealous", "lazy", "mysterious", "nervous", "obnoxious", "panicky",
    "repulsive", "scary", "thoughtless", "uptight", "worried", "agreeable", "brave", "calm",
    "delightful", "eager", "faithful", "gentle", "happy", "jolly", "kind", "lively", "nice",
    "obedient", "proud", "relieved", "silly", "thankful", "victorious", "witty", "zealous",
    "broad", "chubby", "crooked", "curved", "deep", "flat", "high", "hollow", "low", "narrow",
    "round", "shallow", "skinny", "square", "steep", "straight", "wide", "big", "colossal",
    "fat", "gigantic", "great", "huge", "immense", "large", "little", "mammoth", "massive", "miniature",
    "petite", "puny", "scrawny", "short", "small", "tall", "teeny", "tiny", "cooing",
    "deafening", "faint", "hissing", "loud", "melodic", "noisy", "purring", "quiet", "raspy",
    "screeching", "thundering", "voiceless", "whispering", "ancient", "brief", "early", "fast", "late", "long",
    "modern", "old", "old-fashioned", "quick", "rapid", "short", "slow", "swift", "young"
  )
  val nouns = List(
    "able", "achieve", "acoustics", "action", "activity", "aftermath", "afternoon", "afterthought", "apparel",
    "appliance", "beginner", "believe", "bomb", "border", "boundary", "breakfast", "cabbage", "cable",
    "calculator", "calendar", "caption", "carpenter", "cemetery", "channel", "circle", "creator", "creature",
    "education", "faucet", "feather", "friction", "fruit", "fuel", "galley", "guide", "guitar", "health", "heart",
    "idea", "kitten", "laborer", "language", "lawyer", "linen", "locket", "lumber", "magic", "minister", "mitten",
    "money", "mountain", "music", "partner", "passenger", "pickle", "picture", "plantation", "plastic", "pleasure",
    "pocket", "police", "pollution", "railway", "recess", "reward", "route", "scene", "scent", "squirrel", "stranger",
    "suit", "sweater", "temper", "territory", "texture", "thread", "treatment", "veil", "vein", "volcano", "wealth",
    "weather", "wilderness", "wren", "wrist", "writer"
  )
}
