document.querySelector(".cb-form").addEventListener("submit", async function(e) {

  e.preventDefault();

  const title = document.getElementById("title").value.trim();
  const summary = document.getElementById("desc").value.trim();
  const genreId = document.getElementById("category").value;

  // collect hashtags
  const hashtags = [];
  document.querySelectorAll(".cb-chip").forEach(chip => {
      hashtags.push(chip.textContent.replace("×","").trim());
  });

  const bookData = {
      title: title,
      summary: summary,
      publishDate: new Date().toISOString().split("T")[0],
      genreIds: [parseInt(genreId)],
      hashtags: hashtags
  };

  try {

      const response = await fetch("http://localhost:8080/api/books", {
          method: "POST",
          headers: {
              "Content-Type": "application/json",
              "Authorization": "Bearer " + localStorage.getItem("token")
          },
          body: JSON.stringify(bookData)
      });

      if (!response.ok) {
          throw new Error("Failed to create book");
      }

      const data = await response.json();
      console.log("Book created:", data);

      alert("Book created successfully!");
      window.location.href = "/profile.html";

  } catch (error) {

      console.error(error);
      alert("Error creating book");

  }

});