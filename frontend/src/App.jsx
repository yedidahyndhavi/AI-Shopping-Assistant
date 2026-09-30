import { useEffect, useState } from "react";
import "./App.css";

function App() {

  // ============================================================
  // SEARCH STATE
  // ============================================================

  const [searchTerm, setSearchTerm] = useState("");
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");


  // ============================================================
  // RECOMMENDATION STATE
  // ============================================================

  const [category, setCategory] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [minRating, setMinRating] = useState("");
  const [preferredBrand, setPreferredBrand] = useState("");

  const [recommendation, setRecommendation] =
    useState(null);

  const [recommendationLoading, setRecommendationLoading] =
    useState(false);

  const [recommendationError, setRecommendationError] =
    useState("");


  // ============================================================
  // COMPARISON STATE
  // ============================================================

  const [allProducts, setAllProducts] = useState([]);
  const [productsLoading, setProductsLoading] = useState(false);

  const [product1Id, setProduct1Id] = useState("");
  const [product2Id, setProduct2Id] = useState("");

  const [comparison, setComparison] =
    useState(null);

  const [comparisonLoading, setComparisonLoading] =
    useState(false);

  const [comparisonError, setComparisonError] =
    useState("");


  // ============================================================
  // LOAD AVAILABLE PRODUCTS
  // ============================================================

  useEffect(() => {

    const loadProducts = async () => {

      setProductsLoading(true);

      try {

        const response = await fetch(
          "http://localhost:8080/api/products/available"
        );

        if (!response.ok) {
          throw new Error(
            "Failed to load products"
          );
        }

        const data = await response.json();

        setAllProducts(data);

      } catch (error) {

        console.error(
          "Product loading error:",
          error
        );

      } finally {

        setProductsLoading(false);

      }
    };

    loadProducts();

  }, []);


  // ============================================================
  // SEARCH PRODUCTS
  // ============================================================

  const searchProducts = async () => {

    if (!searchTerm.trim()) {

      setError(
        "Please enter a product name."
      );

      return;
    }

    setLoading(true);
    setError("");
    setProducts([]);

    try {

      const response = await fetch(
        `http://localhost:8080/api/products/search?name=${encodeURIComponent(
          searchTerm
        )}`
      );

      if (!response.ok) {

        throw new Error(
          "Failed to fetch products"
        );

      }

      const data = await response.json();

      setProducts(data);

      if (data.length === 0) {

        setError(
          "No products found."
        );

      }

    } catch (error) {

      console.error(
        "Search error:",
        error
      );

      setError(
        "Unable to connect to the product service."
      );

    } finally {

      setLoading(false);

    }
  };


  // ============================================================
  // GET RECOMMENDATION
  // ============================================================

  const getRecommendation = async () => {

    if (!category.trim()) {

      setRecommendationError(
        "Please enter a product category."
      );

      return;
    }

    if (!maxPrice) {

      setRecommendationError(
        "Please enter your maximum price."
      );

      return;
    }

    if (!minRating) {

      setRecommendationError(
        "Please enter your minimum rating."
      );

      return;
    }

    setRecommendationLoading(true);
    setRecommendationError("");
    setRecommendation(null);

    try {

      const response = await fetch(
        "http://localhost:8080/api/products/recommend",
        {
          method: "POST",

          headers: {
            "Content-Type": "application/json",
          },

          body: JSON.stringify({

            category: category,

            maxPrice: Number(maxPrice),

            minRating: Number(minRating),

            priceWeight: 0.4,

            ratingWeight: 0.6,

            preferredBrand:
              preferredBrand.trim() || null,

          }),
        }
      );

      if (!response.ok) {

        const message =
          await response.text();

        throw new Error(
          message ||
          "Failed to get recommendation"
        );

      }

      const data =
        await response.json();

      console.log(
        "Recommendation API response:",
        data
      );

      setRecommendation(data);

    } catch (error) {

      console.error(
        "Recommendation error:",
        error
      );

      setRecommendationError(
        error.message ||
        "Unable to get recommendation."
      );

    } finally {

      setRecommendationLoading(false);

    }
  };


  // ============================================================
  // COMPARE PRODUCTS
  // ============================================================

  const compareProducts = async () => {

    if (!product1Id || !product2Id) {

      setComparisonError(
        "Please select both products."
      );

      return;
    }

    if (product1Id === product2Id) {

      setComparisonError(
        "Please select two different products."
      );

      return;
    }

    setComparisonLoading(true);
    setComparisonError("");
    setComparison(null);

    try {

      const response = await fetch(
        `http://localhost:8080/api/products/compare/detailed?ids=${product1Id},${product2Id}`
      );

      if (!response.ok) {

        const message =
          await response.text();

        throw new Error(
          message ||
          "Failed to compare products"
        );

      }

      const data =
        await response.json();

      console.log(
        "Comparison API response:",
        data
      );

      setComparison(data);

    } catch (error) {

      console.error(
        "Comparison error:",
        error
      );

      setComparisonError(
        error.message ||
        "Unable to compare products."
      );

    } finally {

      setComparisonLoading(false);

    }
  };


  // ============================================================
  // UI
  // ============================================================

  return (

    <div className="app">

      {/* ======================================================
          HEADER
      ====================================================== */}

      <h1>
        IntelliBuy
      </h1>

      <p>
        AI-Powered Product Intelligence &
        Recommendation Platform
      </p>


      {/* ======================================================
          SEARCH
      ====================================================== */}

      <div className="search-container">

        <input
          type="text"
          placeholder="What product are you looking for?"
          value={searchTerm}
          onChange={(event) =>
            setSearchTerm(event.target.value)
          }
        />

        <button
          onClick={searchProducts}
        >
          Search Products
        </button>

      </div>


      {loading && (
        <p>
          Searching products...
        </p>
      )}


      {error && (
        <p>
          {error}
        </p>
      )}


      {/* ======================================================
          SEARCH RESULTS
      ====================================================== */}

      <div className="products">

        {products.map((product) => (

          <div
            className="product-card"
            key={product.id}
          >

            <h2>
              {product.name}
            </h2>

            <p>
              Brand: {product.brand}
            </p>

            <p>
              Price: ₹
              {product.price.toLocaleString("en-IN")}
            </p>

            <p>
              Rating: ⭐ {product.rating.toFixed(1)}
            </p>

            <p>
              Category: {product.category}
            </p>

            <p>
              {product.description}
            </p>

            <p>
              Availability:{" "}
              {product.available
                ? "Available"
                : "Out of Stock"}
            </p>

          </div>

        ))}

      </div>


      {/* ======================================================
          RECOMMENDATION SECTION
      ====================================================== */}

      <div className="recommendation-section">

        <h2>
          Get a Smart Recommendation
        </h2>

        <input
          type="text"
          placeholder="Category (e.g. Smartphone)"
          value={category}
          onChange={(event) =>
            setCategory(event.target.value)
          }
        />

        <input
          type="number"
          placeholder="Maximum price"
          value={maxPrice}
          onChange={(event) =>
            setMaxPrice(event.target.value)
          }
        />

        <input
          type="number"
          step="0.1"
          min="0"
          max="5"
          placeholder="Minimum rating"
          value={minRating}
          onChange={(event) =>
            setMinRating(event.target.value)
          }
        />

        <input
          type="text"
          placeholder="Preferred brand (optional)"
          value={preferredBrand}
          onChange={(event) =>
            setPreferredBrand(event.target.value)
          }
        />

        <button
          onClick={getRecommendation}
        >
          Get Recommendation
        </button>

      </div>


      {recommendationLoading && (
        <p>
          Finding the best product...
        </p>
      )}


      {recommendationError && (
        <p>
          {recommendationError}
        </p>
      )}


      {/* ======================================================
          RECOMMENDATION RESULT
      ====================================================== */}

      {recommendation &&
        recommendation.recommendedProduct && (

        <div className="recommendation-card">

          <h2>
            Recommended Product
          </h2>

          <h3>
            {
              recommendation
                .recommendedProduct
                .name
            }
          </h3>

          <p>
            Brand:{" "}
            {
              recommendation
                .recommendedProduct
                .brand
            }
          </p>

          <p>
            Price: ₹
            {
              recommendation
                .recommendedProduct
                .price
                .toLocaleString("en-IN")
            }
          </p>

          <p>
            Rating: ⭐{" "}
            {
              recommendation
                .recommendedProduct
                .rating
                .toFixed(1)
            }
          </p>

          <p>
            Category:{" "}
            {
              recommendation
                .recommendedProduct
                .category
            }
          </p>

          <p>
            Availability:{" "}
            {
              recommendation
                .recommendedProduct
                .available
                ? "Available"
                : "Out of Stock"
            }
          </p>

          <p>
            Score:{" "}
            {Number(
              recommendation.score
            ).toFixed(2)}
          </p>

          <p>
            {recommendation.reason}
          </p>


          {recommendation.explanation && (

            <div>

              <p>
                {
                  recommendation
                    .explanation
                    .budgetMessage
                }
              </p>

              <p>
                {
                  recommendation
                    .explanation
                    .ratingMessage
                }
              </p>

              <p>
                {
                  recommendation
                    .explanation
                    .preferenceMessage
                }
              </p>

              <p>
                {
                  recommendation
                    .explanation
                    .brandMessage
                }
              </p>

            </div>

          )}

        </div>

      )}


      {/* ======================================================
          COMPARISON SELECTION
      ====================================================== */}

      <div className="recommendation-section">

        <h2>
          Compare Products
        </h2>


        {productsLoading ? (

          <p>
            Loading available products...
          </p>

        ) : (

          <>

            <select
              value={product1Id}
              onChange={(event) =>
                setProduct1Id(event.target.value)
              }
            >

              <option value="">
                Select first product
              </option>

              {allProducts.map((product) => (

                <option
                  key={product.id}
                  value={product.id}
                >
                  {product.name} - ₹
                  {product.price.toLocaleString("en-IN")}
                </option>

              ))}

            </select>


            <select
              value={product2Id}
              onChange={(event) =>
                setProduct2Id(event.target.value)
              }
            >

              <option value="">
                Select second product
              </option>

              {allProducts.map((product) => (

                <option
                  key={product.id}
                  value={product.id}
                >
                  {product.name} - ₹
                  {product.price.toLocaleString("en-IN")}
                </option>

              ))}

            </select>


            <button
              onClick={compareProducts}
            >
              Compare Products
            </button>

          </>

        )}

      </div>


      {comparisonLoading && (
        <p>
          Comparing products...
        </p>
      )}


      {comparisonError && (
        <p>
          {comparisonError}
        </p>
      )}


      {/* ======================================================
          COMPARISON RESULT
      ====================================================== */}

      {comparison &&
        comparison.product1 &&
        comparison.product2 && (

        <div className="comparison-card">

          <h2>
            Product Comparison
          </h2>


          <table className="comparison-table">

            <thead>

              <tr>

                <th>
                  Feature
                </th>

                <th>
                  {comparison.product1.name}
                </th>

                <th>
                  {comparison.product2.name}
                </th>

              </tr>

            </thead>


            <tbody>

              <tr>

                <td>
                  Brand
                </td>

                <td>
                  {comparison.product1.brand}
                </td>

                <td>
                  {comparison.product2.brand}
                </td>

              </tr>


              <tr>

                <td>
                  Price
                </td>

                <td>
                  ₹
                  {comparison.product1.price.toLocaleString(
                    "en-IN"
                  )}
                </td>

                <td>
                  ₹
                  {comparison.product2.price.toLocaleString(
                    "en-IN"
                  )}
                </td>

              </tr>


              <tr>

                <td>
                  Rating
                </td>

                <td>
                  ⭐{" "}
                  {comparison.product1.rating.toFixed(1)}
                </td>

                <td>
                  ⭐{" "}
                  {comparison.product2.rating.toFixed(1)}
                </td>

              </tr>


              <tr>

                <td>
                  Category
                </td>

                <td>
                  {comparison.product1.category}
                </td>

                <td>
                  {comparison.product2.category}
                </td>

              </tr>


              <tr>

                <td>
                  Availability
                </td>

                <td>
                  {comparison.product1.available
                    ? "Available"
                    : "Out of Stock"}
                </td>

                <td>
                  {comparison.product2.available
                    ? "Available"
                    : "Out of Stock"}
                </td>

              </tr>


              <tr>

                <td>
                  Overall Score
                </td>

                <td>
                  {Number(
                    comparison.product1Score
                  ).toFixed(2)}
                </td>

                <td>
                  {Number(
                    comparison.product2Score
                  ).toFixed(2)}
                </td>

              </tr>

            </tbody>

          </table>


          {/* ==================================================
              COMPARISON INSIGHTS
          ================================================== */}

          <div className="comparison-details">

            <h3>
              Comparison Insights
            </h3>

            <p>
              Price Difference: ₹
              {Number(
                comparison.priceDifference
              ).toLocaleString("en-IN")}
            </p>

            <p>
              Rating Difference:{" "}
              {Number(
                comparison.ratingDifference
              ).toFixed(2)}
            </p>


            {comparison.cheaperProduct && (

              <p>
                Cheaper Product:{" "}
                <strong>
                  {comparison.cheaperProduct.name}
                </strong>
              </p>

            )}


            {comparison.higherRatedProduct && (

              <p>
                Higher Rated Product:{" "}
                <strong>
                  {comparison.higherRatedProduct.name}
                </strong>
              </p>

            )}


            <p>
              <strong>
                {comparison.comparisonSummary}
              </strong>
            </p>

          </div>

        </div>

      )}

    </div>
  );
}

export default App;