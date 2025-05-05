package com.example.foodorder.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.foodorder.model.*
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FoodOrder.db"

        // User table
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USER_NAME = "name"
        private const val COLUMN_USER_EMAIL = "email"
        private const val COLUMN_USER_PASSWORD = "password"
        private const val COLUMN_USER_PHONE = "phone"
        private const val COLUMN_USER_ADDRESS = "address"
        private const val COLUMN_USER_IMAGE = "image"
        private const val COLUMN_USER_TYPE = "user_type" // "customer" or "seller"

        // Product table
        private const val TABLE_PRODUCTS = "products"
        private const val COLUMN_PRODUCT_ID = "id"
        private const val COLUMN_PRODUCT_NAME = "name"
        private const val COLUMN_PRODUCT_DESCRIPTION = "description"
        private const val COLUMN_PRODUCT_PRICE = "price"
        private const val COLUMN_PRODUCT_IMAGE = "image"
        private const val COLUMN_PRODUCT_CATEGORY = "category" // "food" or "drink"
        private const val COLUMN_PRODUCT_SELLER_ID = "seller_id"
        private const val COLUMN_PRODUCT_QUANTITY_SOLD = "quantity_sold"

        // Cart table
        private const val TABLE_CART = "cart"
        private const val COLUMN_CART_ID = "id"
        private const val COLUMN_CART_USER_ID = "user_id"
        private const val COLUMN_CART_PRODUCT_ID = "product_id"
        private const val COLUMN_CART_QUANTITY = "quantity"

        // Order table
        private const val TABLE_ORDERS = "orders"
        private const val COLUMN_ORDER_ID = "id"
        private const val COLUMN_ORDER_USER_ID = "user_id"
        private const val COLUMN_ORDER_DATE = "date"
        private const val COLUMN_ORDER_TOTAL = "total"
        private const val COLUMN_ORDER_STATUS = "status" // "placed", "confirmed", "delivered"

        // Order detail table
        private const val TABLE_ORDER_DETAILS = "order_details"
        private const val COLUMN_ORDER_DETAIL_ID = "id"
        private const val COLUMN_ORDER_DETAIL_ORDER_ID = "order_id"
        private const val COLUMN_ORDER_DETAIL_PRODUCT_ID = "product_id"
        private const val COLUMN_ORDER_DETAIL_QUANTITY = "quantity"
        private const val COLUMN_ORDER_DETAIL_PRICE = "price"

        // Notification table
        private const val TABLE_NOTIFICATIONS = "notifications"
        private const val COLUMN_NOTIFICATION_ID = "id"
        private const val COLUMN_NOTIFICATION_USER_ID = "user_id"
        private const val COLUMN_NOTIFICATION_TITLE = "title"
        private const val COLUMN_NOTIFICATION_MESSAGE = "message"
        private const val COLUMN_NOTIFICATION_DATE = "date"
        private const val COLUMN_NOTIFICATION_READ = "is_read"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create User table
        val createUserTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_PHONE + " TEXT,"
                + COLUMN_USER_ADDRESS + " TEXT,"
                + COLUMN_USER_IMAGE + " TEXT,"
                + COLUMN_USER_TYPE + " TEXT" + ")")

        // Create Product table
        val createProductTable = ("CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PRODUCT_NAME + " TEXT,"
                + COLUMN_PRODUCT_DESCRIPTION + " TEXT,"
                + COLUMN_PRODUCT_PRICE + " REAL,"
                + COLUMN_PRODUCT_IMAGE + " TEXT,"
                + COLUMN_PRODUCT_CATEGORY + " TEXT,"
                + COLUMN_PRODUCT_SELLER_ID + " INTEGER,"
                + COLUMN_PRODUCT_QUANTITY_SOLD + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + COLUMN_PRODUCT_SELLER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))")

        // Create Cart table
        val createCartTable = ("CREATE TABLE " + TABLE_CART + "("
                + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CART_USER_ID + " INTEGER,"
                + COLUMN_CART_PRODUCT_ID + " INTEGER,"
                + COLUMN_CART_QUANTITY + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_CART_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_CART_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + "))")

        // Create Order table
        val createOrderTable = ("CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ORDER_USER_ID + " INTEGER,"
                + COLUMN_ORDER_DATE + " TEXT,"
                + COLUMN_ORDER_TOTAL + " REAL,"
                + COLUMN_ORDER_STATUS + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))")

        // Create Order Detail table
        val createOrderDetailTable = ("CREATE TABLE " + TABLE_ORDER_DETAILS + "("
                + COLUMN_ORDER_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ORDER_DETAIL_ORDER_ID + " INTEGER,"
                + COLUMN_ORDER_DETAIL_PRODUCT_ID + " INTEGER,"
                + COLUMN_ORDER_DETAIL_QUANTITY + " INTEGER,"
                + COLUMN_ORDER_DETAIL_PRICE + " REAL,"
                + "FOREIGN KEY(" + COLUMN_ORDER_DETAIL_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_ORDER_DETAIL_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + "))")

        // Create Notification table
        val createNotificationTable = ("CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTIFICATION_USER_ID + " INTEGER,"
                + COLUMN_NOTIFICATION_TITLE + " TEXT,"
                + COLUMN_NOTIFICATION_MESSAGE + " TEXT,"
                + COLUMN_NOTIFICATION_DATE + " TEXT,"
                + COLUMN_NOTIFICATION_READ + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + COLUMN_NOTIFICATION_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))")

        db.execSQL(createUserTable)
        db.execSQL(createProductTable)
        db.execSQL(createCartTable)
        db.execSQL(createOrderTable)
        db.execSQL(createOrderDetailTable)
        db.execSQL(createNotificationTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_DETAILS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // USER OPERATIONS
    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, user.name)
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)
        values.put(COLUMN_USER_PHONE, user.phone)
        values.put(COLUMN_USER_ADDRESS, user.address)
        values.put(COLUMN_USER_IMAGE, user.image)
        values.put(COLUMN_USER_TYPE, user.userType)

        val id = db.insert(TABLE_USERS, null, values)
        db.close()
        return id
    }

    fun getUserByEmail(email: String): User? {
        val db = this.readableDatabase
        var user: User? = null

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD,
                COLUMN_USER_PHONE, COLUMN_USER_ADDRESS, COLUMN_USER_IMAGE, COLUMN_USER_TYPE),
            "$COLUMN_USER_EMAIL = ?", arrayOf(email),
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)),
                image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_IMAGE)),
                userType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_TYPE))
            )
        }

        cursor?.close()
        return user
    }

    fun getUserById(id: Int): User? {
        val db = this.readableDatabase
        var user: User? = null

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD,
                COLUMN_USER_PHONE, COLUMN_USER_ADDRESS, COLUMN_USER_IMAGE, COLUMN_USER_TYPE),
            "$COLUMN_USER_ID = ?", arrayOf(id.toString()),
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)),
                image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_IMAGE)),
                userType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_TYPE))
            )
        }

        cursor?.close()
        return user
    }

    fun updateUser(user: User): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, user.name)
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)
        values.put(COLUMN_USER_PHONE, user.phone)
        values.put(COLUMN_USER_ADDRESS, user.address)
        values.put(COLUMN_USER_IMAGE, user.image)

        return db.update(
            TABLE_USERS,
            values,
            "$COLUMN_USER_ID = ?",
            arrayOf(user.id.toString())
        )
    }

    // PRODUCT OPERATIONS
    fun addProduct(product: Product): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PRODUCT_NAME, product.name)
        values.put(COLUMN_PRODUCT_DESCRIPTION, product.description)
        values.put(COLUMN_PRODUCT_PRICE, product.price)
        values.put(COLUMN_PRODUCT_IMAGE, product.image)
        values.put(COLUMN_PRODUCT_CATEGORY, product.category)
        values.put(COLUMN_PRODUCT_SELLER_ID, product.sellerId)
        values.put(COLUMN_PRODUCT_QUANTITY_SOLD, product.quantitySold)

        val id = db.insert(TABLE_PRODUCTS, null, values)
        db.close()
        return id
    }

    fun updateProduct(product: Product): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PRODUCT_NAME, product.name)
        values.put(COLUMN_PRODUCT_DESCRIPTION, product.description)
        values.put(COLUMN_PRODUCT_PRICE, product.price)
        values.put(COLUMN_PRODUCT_IMAGE, product.image)
        values.put(COLUMN_PRODUCT_CATEGORY, product.category)
        values.put(COLUMN_PRODUCT_QUANTITY_SOLD, product.quantitySold)

        return db.update(
            TABLE_PRODUCTS,
            values,
            "$COLUMN_PRODUCT_ID = ?",
            arrayOf(product.id.toString())
        )
    }

    fun deleteProduct(productId: Int): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_PRODUCTS,
            "$COLUMN_PRODUCT_ID = ?",
            arrayOf(productId.toString())
        )
    }

    fun getAllProducts(): List<Product> {
        val products = ArrayList<Product>()
        val selectQuery = "SELECT * FROM $TABLE_PRODUCTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                    image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE)),
                    category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                    sellerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_SELLER_ID)),
                    quantitySold = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY_SOLD))
                )
                products.add(product)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return products
    }

    fun getProductsByCategory(category: String): List<Product> {
        val products = ArrayList<Product>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_PRODUCT_CATEGORY = ?", arrayOf(category),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                    image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE)),
                    category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                    sellerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_SELLER_ID)),
                    quantitySold = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY_SOLD))
                )
                products.add(product)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return products
    }

    fun getProductsBySeller(sellerId: Int): List<Product> {
        val products = ArrayList<Product>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_PRODUCT_SELLER_ID = ?", arrayOf(sellerId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                    image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE)),
                    category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                    sellerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_SELLER_ID)),
                    quantitySold = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY_SOLD))
                )
                products.add(product)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return products
    }

    fun searchProducts(query: String): List<Product> {
        val products = ArrayList<Product>()
        val db = this.readableDatabase

        val searchQuery = "SELECT * FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_NAME LIKE '%$query%' OR $COLUMN_PRODUCT_DESCRIPTION LIKE '%$query%'"
        val cursor = db.rawQuery(searchQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                    image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE)),
                    category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                    sellerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_SELLER_ID)),
                    quantitySold = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY_SOLD))
                )
                products.add(product)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return products
    }

    // CART OPERATIONS
    fun addToCart(cart: Cart): Long {
        val db = this.writableDatabase

        // Check if product already exists in cart
        val cursor = db.query(
            TABLE_CART,
            arrayOf(COLUMN_CART_ID, COLUMN_CART_QUANTITY),
            "$COLUMN_CART_USER_ID = ? AND $COLUMN_CART_PRODUCT_ID = ?",
            arrayOf(cart.userId.toString(), cart.productId.toString()),
            null, null, null
        )

        return if (cursor != null && cursor.moveToFirst()) {
            // Update existing cart item
            val cartId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_ID))
            val currentQty = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY))

            val values = ContentValues()
            values.put(COLUMN_CART_QUANTITY, currentQty + cart.quantity)

            db.update(TABLE_CART, values, "$COLUMN_CART_ID = ?", arrayOf(cartId.toString()))
            cursor.close()
            cartId.toLong()
        } else {
            // Add new cart item
            val values = ContentValues()
            values.put(COLUMN_CART_USER_ID, cart.userId)
            values.put(COLUMN_CART_PRODUCT_ID, cart.productId)
            values.put(COLUMN_CART_QUANTITY, cart.quantity)

            val id = db.insert(TABLE_CART, null, values)
            cursor?.close()
            id
        }
    }

    fun updateCartItemQuantity(cartId: Int, quantity: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CART_QUANTITY, quantity)

        return db.update(
            TABLE_CART,
            values,
            "$COLUMN_CART_ID = ?",
            arrayOf(cartId.toString())
        )
    }

    fun removeFromCart(cartId: Int): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_CART,
            "$COLUMN_CART_ID = ?",
            arrayOf(cartId.toString())
        )
    }

    fun getCartItems(userId: Int): List<CartItem> {
        val cartItems = ArrayList<CartItem>()
        val db = this.readableDatabase

        val query = "SELECT c.*, p.name, p.price, p.image FROM $TABLE_CART c " +
                "LEFT JOIN $TABLE_PRODUCTS p ON c.$COLUMN_CART_PRODUCT_ID = p.$COLUMN_PRODUCT_ID " +
                "WHERE c.$COLUMN_CART_USER_ID = ?"

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val cartItem = CartItem(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_ID)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_USER_ID)),
                    productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_PRODUCT_ID)),
                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY)),
                    productName = cursor.getString(4), // name column in join
                    productPrice = cursor.getDouble(5), // price column in join
                    productImage = cursor.getString(6)  // image column in join
                )
                cartItems.add(cartItem)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return cartItems
    }

    fun clearCart(userId: Int): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_CART,
            "$COLUMN_CART_USER_ID = ?",
            arrayOf(userId.toString())
        )
    }

    // ORDER OPERATIONS
    fun createOrder(order: Order, orderDetails: List<OrderDetail>): Long {
        val db = this.writableDatabase
        db.beginTransaction()

        try {
            // Insert order
            val orderValues = ContentValues()
            orderValues.put(COLUMN_ORDER_USER_ID, order.userId)
            orderValues.put(COLUMN_ORDER_DATE, order.date)
            orderValues.put(COLUMN_ORDER_TOTAL, order.total)
            orderValues.put(COLUMN_ORDER_STATUS, order.status)

            val orderId = db.insert(TABLE_ORDERS, null, orderValues)

            // Insert order details
            for (detail in orderDetails) {
                val detailValues = ContentValues()
                detailValues.put(COLUMN_ORDER_DETAIL_ORDER_ID, orderId)
                detailValues.put(COLUMN_ORDER_DETAIL_PRODUCT_ID, detail.productId)
                detailValues.put(COLUMN_ORDER_DETAIL_QUANTITY, detail.quantity)
                detailValues.put(COLUMN_ORDER_DETAIL_PRICE, detail.price)

                db.insert(TABLE_ORDER_DETAILS, null, detailValues)

                // Update product sold quantity
                val product = getProductById(detail.productId)
                product?.let {
                    it.quantitySold += detail.quantity
                    updateProduct(it)
                }
            }

            db.setTransactionSuccessful()
            return orderId
        } finally {
            db.endTransaction()
        }
    }

    fun getProductById(productId: Int): Product? {
        val db = this.readableDatabase
        var product: Product? = null

        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_PRODUCT_ID = ?", arrayOf(productId.toString()),
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            product = Product(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                sellerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_SELLER_ID)),
                quantitySold = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY_SOLD))
            )
        }

        cursor?.close()
        return product
    }

    fun updateOrderStatus(orderId: Int, status: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ORDER_STATUS, status)

        return db.update(
            TABLE_ORDERS,
            values,
            "$COLUMN_ORDER_ID = ?",
            arrayOf(orderId.toString())
        )
    }

    fun getUserOrders(userId: Int): List<Order> {
        val orders = ArrayList<Order>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_ORDERS,
            null,
            "$COLUMN_ORDER_USER_ID = ?", arrayOf(userId.toString()),
            null, null, "$COLUMN_ORDER_DATE DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val order = Order(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_USER_ID)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE)),
                    total = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL)),
                    status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS))
                )
                orders.add(order)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return orders
    }

    fun getSellerOrders(sellerId: Int): List<Order> {
        val orders = HashSet<Order>()
        val db = this.readableDatabase

        // Get all order IDs that contain products from this seller
        val query = "SELECT DISTINCT o.* FROM $TABLE_ORDERS o " +
                "JOIN $TABLE_ORDER_DETAILS od ON o.$COLUMN_ORDER_ID = od.$COLUMN_ORDER_DETAIL_ORDER_ID " +
                "JOIN $TABLE_PRODUCTS p ON od.$COLUMN_ORDER_DETAIL_PRODUCT_ID = p.$COLUMN_PRODUCT_ID " +
                "WHERE p.$COLUMN_PRODUCT_SELLER_ID = ? " +
                "ORDER BY o.$COLUMN_ORDER_DATE DESC"

        val cursor = db.rawQuery(query, arrayOf(sellerId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val order = Order(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_USER_ID)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE)),
                    total = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL)),
                    status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS))
                )
                orders.add(order)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return orders.toList()
    }

    fun getOrderById(orderId: Int): Order? {
        val db = this.readableDatabase
        var order: Order? = null

        val cursor = db.query(
            TABLE_ORDERS,
            null,
            "$COLUMN_ORDER_ID = ?", arrayOf(orderId.toString()),
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            order = Order(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_USER_ID)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE)),
                total = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS))
            )
        }

        cursor?.close()
        return order
    }

    fun getOrderDetails(orderId: Int): List<OrderDetail> {
        val orderDetails = ArrayList<OrderDetail>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_ORDER_DETAILS,
            null,
            "$COLUMN_ORDER_DETAIL_ORDER_ID = ?", arrayOf(orderId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val orderDetail = OrderDetail(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_ID)),
                    orderId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_ORDER_ID)),
                    productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_PRODUCT_ID)),
                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_QUANTITY)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_PRICE))
                )
                orderDetails.add(orderDetail)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return orderDetails
    }

    fun getOrderDetailWithProductInfo(orderId: Int): List<Map<String, Any>> {
        val orderDetailsWithProduct = ArrayList<Map<String, Any>>()
        val db = this.readableDatabase

        val query = "SELECT od.*, p.name, p.image FROM $TABLE_ORDER_DETAILS od " +
                "LEFT JOIN $TABLE_PRODUCTS p ON od.$COLUMN_ORDER_DETAIL_PRODUCT_ID = p.$COLUMN_PRODUCT_ID " +
                "WHERE od.$COLUMN_ORDER_DETAIL_ORDER_ID = ?"

        val cursor = db.rawQuery(query, arrayOf(orderId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val item = HashMap<String, Any>()
                item["orderDetail"] = OrderDetail(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_ID)),
                    orderId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_ORDER_ID)),
                    productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_PRODUCT_ID)),
                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_QUANTITY)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_PRICE))
                )
                item["productName"] = cursor.getString(5) // name column in join
                item["productImage"] = cursor.getString(6) // image column in join

                orderDetailsWithProduct.add(item)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return orderDetailsWithProduct
    }

    // NOTIFICATION OPERATIONS
    fun addNotification(notification: Notification): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTIFICATION_USER_ID, notification.userId)
        values.put(COLUMN_NOTIFICATION_TITLE, notification.title)
        values.put(COLUMN_NOTIFICATION_MESSAGE, notification.message)
        values.put(COLUMN_NOTIFICATION_DATE, notification.date)
        values.put(COLUMN_NOTIFICATION_READ, if (notification.isRead) 1 else 0)

        val id = db.insert(TABLE_NOTIFICATIONS, null, values)
        db.close()
        return id
    }

    fun getUserNotifications(userId: Int): List<Notification> {
        val notifications = ArrayList<Notification>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_NOTIFICATIONS,
            null,
            "$COLUMN_NOTIFICATION_USER_ID = ?", arrayOf(userId.toString()),
            null, null, "$COLUMN_NOTIFICATION_DATE DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val notification = Notification(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_ID)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_USER_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_TITLE)),
                    message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_MESSAGE)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_DATE)),
                    isRead = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_READ)) == 1
                )
                notifications.add(notification)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return notifications
    }

    fun markNotificationAsRead(notificationId: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTIFICATION_READ, 1)

        return db.update(
            TABLE_NOTIFICATIONS,
            values,
            "$COLUMN_NOTIFICATION_ID = ?",
            arrayOf(notificationId.toString())
        )
    }

    fun getUnreadNotificationCount(userId: Int): Int {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_NOTIFICATIONS WHERE $COLUMN_NOTIFICATION_USER_ID = ? AND $COLUMN_NOTIFICATION_READ = 0"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        return count
    }

    // STATISTICS FOR SELLER
    fun getSellerStatistics(sellerId: Int): Map<String, Any> {
        val stats = HashMap<String, Any>()
        val db = this.readableDatabase

        // Total products
        val productsQuery = "SELECT COUNT(*) FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_SELLER_ID = ?"
        val productsCursor = db.rawQuery(productsQuery, arrayOf(sellerId.toString()))
        var totalProducts = 0
        if (productsCursor.moveToFirst()) {
            totalProducts = productsCursor.getInt(0)
        }
        productsCursor.close()
        stats["totalProducts"] = totalProducts

        // Total revenue
        val revenueQuery = "SELECT SUM(od.$COLUMN_ORDER_DETAIL_PRICE * od.$COLUMN_ORDER_DETAIL_QUANTITY) " +
                "FROM $TABLE_ORDER_DETAILS od " +
                "JOIN $TABLE_PRODUCTS p ON od.$COLUMN_ORDER_DETAIL_PRODUCT_ID = p.$COLUMN_PRODUCT_ID " +
                "JOIN $TABLE_ORDERS o ON od.$COLUMN_ORDER_DETAIL_ORDER_ID = o.$COLUMN_ORDER_ID " +
                "WHERE p.$COLUMN_PRODUCT_SELLER_ID = ? AND o.$COLUMN_ORDER_STATUS != 'cancelled'"
        val revenueCursor = db.rawQuery(revenueQuery, arrayOf(sellerId.toString()))
        var totalRevenue = 0.0
        if (revenueCursor.moveToFirst() && !revenueCursor.isNull(0)) {
            totalRevenue = revenueCursor.getDouble(0)
        }
        revenueCursor.close()
        stats["totalRevenue"] = totalRevenue

        // Total orders
        val ordersQuery = "SELECT COUNT(DISTINCT o.$COLUMN_ORDER_ID) " +
                "FROM $TABLE_ORDERS o " +
                "JOIN $TABLE_ORDER_DETAILS od ON o.$COLUMN_ORDER_ID = od.$COLUMN_ORDER_DETAIL_ORDER_ID " +
                "JOIN $TABLE_PRODUCTS p ON od.$COLUMN_ORDER_DETAIL_PRODUCT_ID = p.$COLUMN_PRODUCT_ID " +
                "WHERE p.$COLUMN_PRODUCT_SELLER_ID = ?"
        val ordersCursor = db.rawQuery(ordersQuery, arrayOf(sellerId.toString()))
        var totalOrders = 0
        if (ordersCursor.moveToFirst()) {
            totalOrders = ordersCursor.getInt(0)
        }
        ordersCursor.close()
        stats["totalOrders"] = totalOrders

        // Top selling products
        val topProductsQuery = "SELECT p.*, SUM(od.$COLUMN_ORDER_DETAIL_QUANTITY) as total_sold " +
                "FROM $TABLE_PRODUCTS p " +
                "JOIN $TABLE_ORDER_DETAILS od ON p.$COLUMN_PRODUCT_ID = od.$COLUMN_ORDER_DETAIL_PRODUCT_ID " +
                "WHERE p.$COLUMN_PRODUCT_SELLER_ID = ? " +
                "GROUP BY p.$COLUMN_PRODUCT_ID " +
                "ORDER BY total_sold DESC LIMIT 5"
        val topProductsCursor = db.rawQuery(topProductsQuery, arrayOf(sellerId.toString()))
        val topProducts = ArrayList<Map<String, Any>>()

        if (topProductsCursor.moveToFirst()) {
            do {
                val product = HashMap<String, Any>()
                product["id"] = topProductsCursor.getInt(topProductsCursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID))
                product["name"] = topProductsCursor.getString(topProductsCursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME))
                product["totalSold"] = topProductsCursor.getInt(topProductsCursor.getColumnIndexOrThrow("total_sold"))
                topProducts.add(product)
            } while (topProductsCursor.moveToNext())
        }
        topProductsCursor.close()
        stats["topProducts"] = topProducts

        return stats
    }
}