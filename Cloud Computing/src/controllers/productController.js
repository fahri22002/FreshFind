const productModel = require('../models/productModel');
const sellerModel = require('../models/sellerModel.js');
const { nanoid } = require('nanoid/non-secure');
const helpers = require('../helpers/helpers.js');
const FormData = require('form-data');
const axios = require('axios');
const fs = require('fs');

const getHomeProducts = async (req, res) => {
  try {
    const products = await productModel.getHomeProducts();

    // Still not correct *stil assuming the array shape
    const productIds = products.map((item) => item.id);
    console.log(productIds);
    const productPhotos = await productModel.getHomeProductsPhoto(productIds);
    res.status(200).json({
      message: 'Success',
      product_datas: products,
      product_photos: productPhotos
    });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Products retrieval fail' });
  }
};

const getProductById = async (req, res) => {
  try {
    const { product_id } = req.body
    const product = await productModel.getProductById(product_id);  
    const photos = await productModel.getProductPhotoById(product_id);
    const seller = await sellerModel.getSellerForProductById(product[0].seller_id);
    if (product.length === 0) {
      res.status(404).json({ error: 'Product not found' });
    } else {
      res.status(200).json({
        message: 'Product found',
        data: {
          productData: product,
          productPhotos: photos,
          sellerData: seller
        }
      });
    }
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Product retrieval fail' });
  }
};

const createProduct = async (req, res) => {
  try {
    const number = req.user.number;
    const id = nanoid(12);
    const { name, price, stock, description, category } = req.body;
    await productModel.createProduct(id, name, price, 0, stock, description, number, category);
    res.status(201).json({ message: 'Product creation success', product_id: id });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Product creation fail' });
  }
};

const createProductPhoto = async (req, res) => {
  const seller_id = req.user.number;
  const newPhoto = req.file;
  const id = nanoid(12);
  const product_id = req.body.productId;
  
  // Verifying product owner
  const verified = await productModel.verifyProductOwner(product_id, seller_id);
  console.log(verified)
  console.log(seller_id)
  console.log(product_id)
  if (typeof verified[0][0] != 'object') {
    return res.status(401).json({message: 'Unauthorized'});
  }

  // Handling image upload
  let imageUrl = '' 
  try {
    imageUrl = await helpers.uploadImage(newPhoto, 'product_photos');
  } catch (error) {
    console.error(error)
    return res.status(500).json({ message: 'Upload fail'});
  }

  // Insert link to database
  try {
    await productModel.createProductPhoto(id, product_id, imageUrl);
    return res.status(200).json({ message: 'Photo adding success', data: imageUrl});
  } catch {
    return res.status(500).json({ message: 'Database update fail' });
  }
};

const updateProductDetails = async (req, res) => {
  const { product_id, name, price, stock, description, category } = req.body;
  const seller_id = req.user.number; // Mengambil seller_id dari token

  if (!product_id) {
      return res.status(400).json({ message: 'Product ID is required' });
  }

  try {
      const result = await productModel.updateProductDetails(product_id, name, price, stock, description, category, seller_id);
      if (result.affectedRows === 0) {
          return res.status(403).json({ message: 'Unauthorized to update this product or Product not found' });
      }
      res.status(200).json({ message: 'Product updated successfully' });
  } catch (error) {
      console.error(error);
      res.status(500).json({ message: 'Failed to update product' });
  }
};


const deleteProduct = async (req, res) => {
  try {
    const [result, _] = await productModel.deleteProduct(req.params.id);
    if (result.affectedRows === 0) {
      res.status(404).json({ error: 'Product not found' });
    } else {
      res.json({ message: 'Product deleted' });
    }
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Internal server error' });
  }
};

const searchProducts = async (req, res) => {
  try {
    const { keyword } = req.body;
    const products = await productModel.getProductsByKeyword(`%${keyword}%`);
    res.status(200).json({ message: 'Success', data: products });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Product retrieval fail' });
  }
};

const predictProductName = async (req, res) => {
  if (!req.file) {
    return res.status(400).json({ message: 'No photo uploaded'});
  } else {
    const newPhoto = req.file;
    const modelUrl = process.env.MODEL_URI;

    // Upload to Bucket
    let imageUrl = '' 
    try {
      imageUrl = await helpers.uploadImage(newPhoto, 'product_recognize_photos');
    } catch (error) {
      throw error;
    }

    try {
      // Retrieve uploaded image
      const image = await axios({
        method: 'get',
        url: imageUrl,
        responseType: 'stream',
      });

      // Request to AI Model
      const form = new FormData();
      form.append('file', image.data);
      let config = {
        method: 'post',
        maxBodyLength: Infinity,
        url: modelUrl,
        headers: { 
          ...form.getHeaders()
        },
        data : form
      };
      
      const response = await axios.request(config);
      const keyword = JSON.stringify(response.data.predicted_class).replace(/"/g, '');

      // Translate Keyword
      const googleTranslateUrl = 'https://translation.googleapis.com/language/translate/v2';
      const translatedKeyword = await axios({
        method: 'get',
        url: `${googleTranslateUrl}?q=${keyword}&target=id&format=text&source=en&model=base&key=${process.env.CLOUD_TRANSLATION_API_KEY}`
      });

      // Process the search
      const result = await axios.post('https://app.freshfind.dev/api/products/search', { keyword: translatedKeyword.data.data.translations[0].translatedText });
      res.status(200).json({ message: 'Success', data: result.data.data});
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: 'Image recognition fail', uri: imageUrl });
    }
  }
};

module.exports = {
  getHomeProducts,
  getProductById,
  createProduct,
  createProductPhoto,
  updateProductDetails,
  deleteProduct,
  searchProducts,
  predictProductName
};