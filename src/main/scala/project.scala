import java.sql.{Connection, DriverManager, PreparedStatement}
import java.time.{LocalDate, LocalDateTime}
import java.time.temporal.ChronoUnit
import java.io.{File, FileOutputStream, PrintWriter}
import scala.io.Source
import scala.io.{BufferedSource, Source}
import java.time.format.DateTimeFormatter
import java.util.logging._
import java.io._

object project extends App {
  // Define the JDBC connection parameters
  val url = "jdbc:oracle:thin:@localhost:1521:XE"
  val username = "HR"
  val password = "123"

  // Set up logging properties
  val logFile = new File("C:\\Users\\shoroukabdelraouf\\IdeaProjects\\scala_project\\logs\\rules_engine.log")
  val fileHandler = new FileHandler(logFile.getPath, true)
  val formatter = new SimpleFormatter()

  // Configure the file handler with formatter
  fileHandler.setFormatter(formatter)

  // Get the root logger and add the file handler
  val logger = Logger.getLogger("")
  logger.addHandler(fileHandler)

  // Set logging level (INFO, WARNING, SEVERE, etc.)
  logger.setLevel(Level.INFO)

  // Function to establish a database connection
  def getConnection(url: String, username: String, password: String): Connection = {
    DriverManager.getConnection(url, username, password)
  }

  val createTableQuery = """
    CREATE TABLE  scalatable (
      timestamp DATE,
      product_name VARCHAR2(255),
      expiry_date DATE,
      quantity NUMBER,
      unit_price NUMBER,
      channel VARCHAR2(255),
      payment_method VARCHAR2(255),
      discount NUMBER,
      price_after NUMBER
    )
  """

  // Open a connection
  val connection = getConnection(url, username, password)
  val preparedStatement = connection.prepareStatement(createTableQuery)

  // Execute the SQL statement to create the table
  preparedStatement.executeUpdate()

  // Read file
  val source: BufferedSource = Source.fromFile("src/main/scala/TRX1000.csv")
  val lines: List[String] = source.getLines().drop(1).toList // drop header


  case class ord(timestamp: LocalDate, product_name: String, expiry_date: LocalDate, quantity: Int, unit_price: Float, channel: String, payment_method: String)

  def getline(line: String): ord = {
    val split_file = line.split(",")
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val purchasing_date = LocalDate.parse(split_file(0).substring(0, split_file(0).indexOf('T')), dateFormat)
    val expiration_date = LocalDate.parse(split_file(2), dateFormat)
    val productName = split_file(1).split("-")(0).trim.toLowerCase

    ord(purchasing_date, productName, expiration_date, split_file(3).toInt, split_file(4).toFloat, split_file(5), split_file(6))
  }

  // function(purchasing_expiration) to calculate the discount based on the expiration date.
  // qualifying rules >> less than 30 days remaining for the product to expire (from the day of transaction, i.e. timestamp)
  // calculation rule >> If 29 days remaining -> 1% discount
  //if 28 days remaining -> 2% discount
  //if 27 days remaining -> 3% discount
  //etc …

  // required: (timestamp-expiry_date)  0,2
  val orders = lines.map(getline)

  val days = orders.map(order => ChronoUnit.DAYS.between(order.timestamp, order.expiry_date).toInt)
  val discountpercentage = days.map(purchasing_expiration)

  def purchasing_expiration(no_of_days: Int): Double = {
    if (no_of_days > 0 && no_of_days < 30) {
      val discountPercentage = (30 - no_of_days)
      discountPercentage.toFloat / 100
    }
    else 0
  }

  // function to calculate the discount based on the product category
  // qualifying rules >> Cheese and wine products are on sale
  // calculation rule >> cheese -> 10% discount
  // wine -> 5% discount

  val discount_percentage_pro = orders.map(order => prod_cate(order.product_name))

  def prod_cate(productName: String): Double = {
    productName match {
      case "cheese" => 0.10
      case "wine" => 0.05
      case _ => 0
    }
  }

  // function to calculate the discount based on a specific purchase day of month
  // qualifying rules >> Products that are sold on 23rd of March have a special discount! (Celebrating the end of java project?)
  // calculation rule >> 50% discount !!
  val specialdiscount = orders.map(order => special_discount(order.timestamp))

  def special_discount(purchasing_date: LocalDate): Double = {
    val mon = purchasing_date.getMonthValue
    val day = purchasing_date.getDayOfMonth
    if (mon == 3 && day == 23) 0.50
    else 0
  }

  // function to calculate the discount based on buying more than 5 of the same product
  // qualifying rules >> buying more than 5 of the same product
  // calculation rule >> 6 – 9 units -> 5% discount
  //10-14 units -> 7% discount
  //More than 15 -> 10% discount
  val num_pro_discount = orders.map(order => no_of_products(order.quantity))

  def no_of_products(quantity: Int): Double = {
    if (quantity >= 6 && quantity <= 9) {0.05}
    else if (quantity >= 10 && quantity <= 14) {0.07}
    else if (quantity >= 15) {0.10}
    else {0}
  }

  // qualifying rules >> Sales that are made through the App will have a special discount
  // calculation rule >> quantity rounded up to the nearest multiple of 5.
  //Ex: if quantity: 1, 2, 3, 4, 5 ‐> discount 5%
  //if quantity 6, 7, 8, 9, 10 ‐> discount 10%
  //if quantity 11, 12, 13, 14, 15 ‐> discount 15%
  //etc …

  val buying_way = orders.map(order => channel_way(order.channel, order.quantity))

  def channel_way(channel: String, quantity: Int): Double = {
    val roundedQuantity = math.ceil(quantity / 5.0) * 5
    if (channel == "App" && roundedQuantity <= 5) {0.05}
    else if (channel == "App" && roundedQuantity <= 10) {0.10}
    else if (channel == "App" && roundedQuantity <= 15) {0.15}
    else {0}
  }

  // qualifying rules >> Sales that are made using Visa cards qualify to a minor discount
  // calculation rule >> 5%
  val discount_visa = orders.map(order => visaDiscount(order.payment_method))

  def visaDiscount(payment: String): Double = {
    if (payment == "Visa") {0.05}
    else {0}
  }

  val total_discounts = orders.map { order =>
    List(
      purchasing_expiration(ChronoUnit.DAYS.between(order.timestamp, order.expiry_date).toInt),
      prod_cate(order.product_name),
      special_discount(order.timestamp),
      no_of_products(order.quantity),
      channel_way(order.channel, order.quantity),
      visaDiscount(order.payment_method)
    ).filter(_ > 0).sorted.reverse.take(2)}

  // if length of total_discounts == 0 then average = 0
  // if length of total_discounts == 1 then average equal the value of this discount
  // if length of total_discounts == 2 then average equal the average of these 2 elements
  val average_discounts = total_discounts.map { discounts =>
    if (discounts.length == 0) {0}
    else if (discounts.length == 1) {discounts.head}
    else {discounts.sum / discounts.length}
  }

  // calculate final price after discount
  // final price =price-(price * discount)
  // add average discounts to he orders line
  val final_prices = orders.zip(average_discounts).map { case (order, discount) =>
    val discountedPrice = (order.unit_price * order.quantity) - ((order.unit_price * order.quantity)* discount).toFloat
    discountedPrice}

  // Define the insert statement
  val insertStatement = """
    INSERT INTO scalatable (
      timestamp, product_name, expiry_date, quantity, unit_price,
      channel, payment_method,  discount, price_after
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
  """

  // Prepare the insert statement
  val insertPreparedStatement = connection.prepareStatement(insertStatement)

  // Iterate over orders and insert each one into the database
  orders.zip(average_discounts).zip(final_prices).foreach { case ((order, avg_discount),finalPrice) =>
    insertPreparedStatement.setDate(1, java.sql.Date.valueOf(order.timestamp))
    insertPreparedStatement.setString(2, order.product_name)
    insertPreparedStatement.setDate(3, java.sql.Date.valueOf(order.expiry_date))
    insertPreparedStatement.setInt(4, order.quantity)
    insertPreparedStatement.setFloat(5, order.unit_price)
    insertPreparedStatement.setString(6, order.channel)
    insertPreparedStatement.setString(7, order.payment_method)
    insertPreparedStatement.setDouble(8, avg_discount) // discount
    insertPreparedStatement.setFloat(9, finalPrice) // price_after

    // Execute the insert statement
    insertPreparedStatement.executeUpdate()
    // Log the insert statement
    val insertValues = s"INSERT INTO scalatable (timestamp, product_name, expiry_date, quantity, unit_price, channel, payment_method, discount, price_after) " +
      s"VALUES ('${order.timestamp}', '${order.product_name}', '${order.expiry_date}', ${order.quantity}, ${order.unit_price}, '${order.channel}', '${order.payment_method}', ${avg_discount}, ${finalPrice})"
    logger.info(insertValues)
  }

  // Close the prepared statement
  insertPreparedStatement.close()

  // Close the file handler to release resources
  fileHandler.close()
}
