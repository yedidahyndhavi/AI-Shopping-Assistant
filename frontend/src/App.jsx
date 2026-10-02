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
  // SMART RECOMMENDATION STATE
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
  // TOP-N RECOMMENDATION STATE
  // ============================================================

  const [topRecommendations, setTopRecommendations] =
    useState([]);

  const [topRecommendationLoading, setTopRecommendationLoading] =
    useState(false);

  const [topRecommendationError, setTopRecommendationError] =
    useState("");


  // ============================================================
  // NATURAL LANGUAGE QUERY STATE
  // ============================================================

  const [naturalLanguageQuery, setNaturalLanguageQuery] =
    useState("");

  const [queryRecommendation, setQueryRecommendation] =
    useState(null);

  const [queryLoading, setQueryLoading] =
    useState(false);

  const [queryError, setQueryError] =
    useState("");


  // ============================================================
  // COMPARISON STATE
  // ============================================================

  const [allProducts, setAllProducts] =
    useState([]);

  const [productsLoading, setProductsLoading] =
    useState(false);

  const [product1Id, setProduct1Id] =
    useState("");

  const [product2Id, setProduct2Id] =
    useState("");

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
  // FORMAT PRICE
  // ============================================================

  const formatPrice = (price) => {

    if (
      price === null ||
      price === undefined ||
      Number.isNaN(Number(price))
    ) {
      return "-";
    }

    return Number(price).toLocaleString("en-IN");
  };


  // ============================================================
  // FORMAT NUMBER
  // ============================================================

  const formatNumber = (value) => {

    if (
      value === null ||
      value === undefined ||
      Number.isNaN(Number(value))
    ) {
      return "-";
    }

    return Number(value).toFixed(2);
  };


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

      const message =
        await response.text();

      if (!response.ok) {

        throw new Error(
          message ||
          "Failed to fetch products"
        );

      }

      const data =
        JSON.parse(message);

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
  // GET SINGLE SMART RECOMMENDATION
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

      const message =
        await response.text();

      if (!response.ok) {

        throw new Error(
          message ||
          "Failed to get recommendation"
        );

      }

      const data =
        JSON.parse(message);

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
  // GET TOP 3 RECOMMENDATIONS
  // ============================================================

  const getTopRecommendations = async () => {

    if (!category.trim()) {

      setTopRecommendationError(
        "Please enter a product category."
      );

      return;
    }

    if (!maxPrice) {

      setTopRecommendationError(
        "Please enter your maximum price."
      );

      return;
    }

    if (!minRating) {

      setTopRecommendationError(
        "Please enter your minimum rating."
      );

      return;
    }

    setTopRecommendationLoading(true);
    setTopRecommendationError("");
    setTopRecommendations([]);

    try {

      const response = await fetch(
        "http://localhost:8080/api/products/recommend/top?limit=3",
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

      const message =
        await response.text();

      if (!response.ok) {

        throw new Error(
          message ||
          "No matching products found."
        );

      }

      const data =
        JSON.parse(message);

      console.log(
        "Top recommendations API response:",
        data
      );


      // ========================================================
      // SUPPORT RECOMMENDATION LIST RESPONSE
      // ========================================================

      let recommendations = [];

      if (
        data &&
        Array.isArray(data.recommendations)
      ) {

        recommendations =
          data.recommendations;

      } else if (Array.isArray(data)) {

        recommendations = data;

      }


      if (recommendations.length === 0) {

        setTopRecommendationError(
          "No matching products found. Try increasing your budget or changing your requirements."
        );

        return;
      }


      setTopRecommendations(
        recommendations
      );

    } catch (error) {

      console.error(
        "Top recommendations error:",
        error
      );

      setTopRecommendationError(
        error.message ||
        "Unable to get top recommendations."
      );

    } finally {

      setTopRecommendationLoading(false);

    }
  };


  // ============================================================
  // NATURAL LANGUAGE SHOPPING QUERY
  // ============================================================

  const getNaturalLanguageRecommendation =
    async () => {

      if (!naturalLanguageQuery.trim()) {

        setQueryError(
          "Please describe what product you are looking for."
        );

        return;
      }

      setQueryLoading(true);
      setQueryError("");
      setQueryRecommendation(null);

      try {

        const response = await fetch(
          "http://localhost:8080/api/products/recommend/query",
          {
            method: "POST",

            headers: {
              "Content-Type": "application/json",
            },

            body: JSON.stringify({
              query: naturalLanguageQuery
            }),
          }
        );

        const message =
          await response.text();

        if (!response.ok) {

          if (response.status === 404) {

            throw new Error(
              "No matching products found. Try increasing your budget or changing your requirements."
            );

          }

          if (response.status === 400) {

            throw new Error(
              message ||
              "Please check your shopping requirements."
            );

          }

          throw new Error(
            message ||
            "Unable to process your shopping request."
          );

        }

        const data =
          JSON.parse(message);

        console.log(
          "Natural language query response:",
          data
        );

        setQueryRecommendation(data);

      } catch (error) {

        console.error(
          "Natural language query error:",
          error
        );

        setQueryError(
          error.message ||
          "Unable to process your shopping request."
        );

      } finally {

        setQueryLoading(false);

      }
    };


  // ============================================================
  // GET QUERY RECOMMENDED PRODUCT
  // ============================================================

  const getQueryProduct = () => {

    if (!queryRecommendation) {
      return null;
    }

    if (
      queryRecommendation.recommendedProduct
    ) {

      return queryRecommendation
        .recommendedProduct;

    }

    if (
      queryRecommendation.product
    ) {

      return queryRecommendation.product;

    }

    return null;
  };


  const queryProduct =
    getQueryProduct();


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

      const message =
        await response.text();

      if (!response.ok) {

        throw new Error(
          message ||
          "Failed to compare products"
        );

      }

      const data =
        JSON.parse(message);

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
          PRODUCT SEARCH
      ====================================================== */}

      <div className="search-container">

        <input
          type="text"
          placeholder="What product are you looking for?"
          value={searchTerm}
          onChange={(event) =>
            setSearchTerm(
              event.target.value
            )
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
              {formatPrice(product.price)}
            </p>

            <p>
              Rating: ⭐{" "}
              {formatNumber(product.rating)}
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
          ASK INTELLIBUY
      ====================================================== */}

      <div className="recommendation-section">

        <h2>
          Ask IntelliBuy
        </h2>

        <p>
          Describe what you are looking for
          in natural language.
        </p>

        <input
          type="text"
          placeholder="Example: I need an Apple smartphone under ₹80000 with rating above 4"
          value={naturalLanguageQuery}
          onChange={(event) =>
            setNaturalLanguageQuery(
              event.target.value
            )
          }
        />

        <button
          onClick={
            getNaturalLanguageRecommendation
          }
        >
          Find My Product
        </button>

      </div>


      {queryLoading && (
        <p>
          Understanding your requirements...
        </p>
      )}

      {queryError && (
        <p>
          {queryError}
        </p>
      )}


      {/* ======================================================
          NATURAL LANGUAGE RESULT
      ====================================================== */}

      {queryRecommendation && (

        <div className="recommendation-card">

          <h2>
            AI Shopping Assistant
          </h2>

          <p>
            <strong>
              Your request:
            </strong>
          </p>

          <p>
            "{naturalLanguageQuery}"
          </p>


          {queryProduct && (

            <>

              <h3>
                {queryProduct.name}
              </h3>

              <p>
                Brand:{" "}
                {queryProduct.brand}
              </p>

              <p>
                Price: ₹
                {formatPrice(
                  queryProduct.price
                )}
              </p>

              <p>
                Rating: ⭐{" "}
                {formatNumber(
                  queryProduct.rating
                )}
              </p>

              <p>
                Category:{" "}
                {queryProduct.category}
              </p>

              <p>
                Availability:{" "}
                {queryProduct.available
                  ? "Available"
                  : "Out of Stock"}
              </p>

            </>

          )}


          {queryRecommendation.score !==
            undefined && (

            <p>
              Recommendation Score:{" "}
              <strong>
                {formatNumber(
                  queryRecommendation.score
                )}
              </strong>
            </p>

          )}


          {queryRecommendation.reason && (

            <p>
              <strong>
                Why this product?
              </strong>

              <br />

              {queryRecommendation.reason}
            </p>

          )}


          {queryRecommendation.explanation && (

            <div>

              {queryRecommendation
                .explanation
                .budgetMessage && (

                <p>
                  {
                    queryRecommendation
                      .explanation
                      .budgetMessage
                  }
                </p>

              )}

              {queryRecommendation
                .explanation
                .ratingMessage && (

                <p>
                  {
                    queryRecommendation
                      .explanation
                      .ratingMessage
                  }
                </p>

              )}

              {queryRecommendation
                .explanation
                .preferenceMessage && (

                <p>
                  {
                    queryRecommendation
                      .explanation
                      .preferenceMessage
                  }
                </p>

              )}

              {queryRecommendation
                .explanation
                .brandMessage && (

                <p>
                  {
                    queryRecommendation
                      .explanation
                      .brandMessage
                  }
                </p>

              )}

            </div>

          )}

        </div>

      )}


      {/* ======================================================
          MANUAL SMART RECOMMENDATION
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
            setCategory(
              event.target.value
            )
          }
        />

        <input
          type="number"
          placeholder="Maximum price"
          value={maxPrice}
          onChange={(event) =>
            setMaxPrice(
              event.target.value
            )
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
            setMinRating(
              event.target.value
            )
          }
        />

        <input
          type="text"
          placeholder="Preferred brand (optional)"
          value={preferredBrand}
          onChange={(event) =>
            setPreferredBrand(
              event.target.value
            )
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
          SINGLE RECOMMENDATION RESULT
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
            {formatPrice(
              recommendation
                .recommendedProduct
                .price
            )}
          </p>

          <p>
            Rating: ⭐{" "}
            {formatNumber(
              recommendation
                .recommendedProduct
                .rating
            )}
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
            {formatNumber(
              recommendation.score
            )}
          </p>

          <p>
            {recommendation.reason}
          </p>


          {recommendation.explanation && (

            <div>

              {recommendation
                .explanation
                .budgetMessage && (

                <p>
                  {
                    recommendation
                      .explanation
                      .budgetMessage
                  }
                </p>

              )}

              {recommendation
                .explanation
                .ratingMessage && (

                <p>
                  {
                    recommendation
                      .explanation
                      .ratingMessage
                  }
                </p>

              )}

              {recommendation
                .explanation
                .preferenceMessage && (

                <p>
                  {
                    recommendation
                      .explanation
                      .preferenceMessage
                  }
                </p>

              )}

              {recommendation
                .explanation
                .brandMessage && (

                <p>
                  {
                    recommendation
                      .explanation
                      .brandMessage
                  }
                </p>

              )}

            </div>

          )}

        </div>

      )}


      {/* ======================================================
          TOP 3 RECOMMENDATIONS
      ====================================================== */}

      <div className="recommendation-section">

        <h2>
          Top 3 Recommendations
        </h2>

        <p>
          Find the best matching products based
          on your category, budget, rating, and
          brand preference.
        </p>

        <button
          onClick={getTopRecommendations}
        >
          Find Top 3 Products
        </button>

      </div>


      {topRecommendationLoading && (

        <p>
          Finding the top 3 products...
        </p>

      )}


      {topRecommendationError && (

        <p>
          {topRecommendationError}
        </p>

      )}


      {topRecommendations.length > 0 && (

        <div className="products">

          {topRecommendations.map(
            (item, index) => {

              const product =
                item.recommendedProduct ||
                item.product ||
                item;

              const score =
                item.score;

              const reason =
                item.reason;


              return (

                <div
                  className="recommendation-card"
                  key={
                    product.id ||
                    index
                  }
                >

                  <h2>
                    #{index + 1}
                  </h2>

                  <h3>
                    {product.name}
                  </h3>

                  <p>
                    Brand:{" "}
                    {product.brand}
                  </p>

                  <p>
                    Price: ₹
                    {formatPrice(
                      product.price
                    )}
                  </p>

                  <p>
                    Rating: ⭐{" "}
                    {formatNumber(
                      product.rating
                    )}
                  </p>

                  <p>
                    Category:{" "}
                    {product.category}
                  </p>

                  <p>
                    Availability:{" "}
                    {product.available
                      ? "Available"
                      : "Out of Stock"}
                  </p>


                  {score !== undefined && (

                    <p>
                      Recommendation Score:{" "}
                      <strong>
                        {formatNumber(
                          score
                        )}
                      </strong>
                    </p>

                  )}


                  {reason && (

                    <p>
                      {reason}
                    </p>

                  )}

                </div>

              );

            }

          )}

        </div>

      )}


      {/* ======================================================
          PRODUCT COMPARISON
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
                setProduct1Id(
                  event.target.value
                )
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
                  {formatPrice(
                    product.price
                  )}
                </option>

              ))}

            </select>


            <select
              value={product2Id}
              onChange={(event) =>
                setProduct2Id(
                  event.target.value
                )
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
                  {formatPrice(
                    product.price
                  )}
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
                  {formatPrice(
                    comparison.product1.price
                  )}
                </td>

                <td>
                  ₹
                  {formatPrice(
                    comparison.product2.price
                  )}
                </td>

              </tr>


              <tr>

                <td>
                  Rating
                </td>

                <td>
                  ⭐{" "}
                  {formatNumber(
                    comparison.product1.rating
                  )}
                </td>

                <td>
                  ⭐{" "}
                  {formatNumber(
                    comparison.product2.rating
                  )}
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
                  {formatNumber(
                    comparison.product1Score
                  )}
                </td>

                <td>
                  {formatNumber(
                    comparison.product2Score
                  )}
                </td>

              </tr>

            </tbody>

          </table>


          <div className="comparison-details">

            <h3>
              Comparison Insights
            </h3>

            <p>
              Price Difference: ₹
              {formatPrice(
                comparison.priceDifference
              )}
            </p>

            <p>
              Rating Difference:{" "}
              {formatNumber(
                comparison.ratingDifference
              )}
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