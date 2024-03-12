package com.example.groceryapp

data class dataMember(
    var id : Int,
    var product_id : Int,
    var namaProduk : String,
    var hargaAsli : Int,
    var hargaMember : Int,
    var hargaDiskon : Int,
    var image : String,
    var jumlah : Int,
)
