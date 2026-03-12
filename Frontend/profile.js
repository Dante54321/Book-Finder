/* --current user data--
   TODO: replace this object with the real API / session data once implemented
   note: empty strings will trigger defaults 
------------------------------------------------------------------------------------*/
var currentUser = {
  username:   "Username1",
  firstName:  "",          // empty → shows "Firstname Lastname"
  lastName:   "",
  avatarUrl:  "",          // empty → shows icon placeholder
  joinedDate: "Feb 2026",
  bio:        "",          // empty → shows fallback message
  books: []                // empty → shows 1 blank card

  // example of a book entry:
  // { title: "Pride & Prejudice", author: "Jane Austen", coverUrl: "https://..." }
};

/* -carousel settings- */
var VISIBLE = 4;    
var CARD_W  = 184;  
var currentIndex = 0;

/* -render user profile- */
function renderProfile(user) {

  document.getElementById("profileUsername").textContent =
    user.username || "Username1";

  var parts = [];
  if (user.firstName) parts.push(user.firstName);
  if (user.lastName)  parts.push(user.lastName);
  document.getElementById("profileFullname").textContent =
    parts.length > 0 ? parts.join(" ") : "Firstname Lastname";

  document.getElementById("profileJoined").textContent =
    user.joinedDate || "—";

  if (user.avatarUrl) {
    var img = document.createElement("img");
    img.src = user.avatarUrl;
    img.alt = user.username;
    var avatarWrap = document.getElementById("profileAvatar");
    avatarWrap.innerHTML = "";
    avatarWrap.appendChild(img);
  }

  var bioEl = document.getElementById("profileBio");
  if (user.bio && user.bio.trim()) {
    bioEl.textContent = user.bio;
    bioEl.classList.remove("empty-state");
  } else {
    bioEl.textContent = "User hasn't created a bio";
    bioEl.classList.add("empty-state");
  }

  renderBooks(user.books || []);
}

/* -render book carousel- */
function renderBooks(books) {
  var track    = document.getElementById("booksTrack");
  var dotsWrap = document.getElementById("carouselDots");

  track.innerHTML    = "";
  dotsWrap.innerHTML = "";
  currentIndex       = 0;

  // **make sure to always show 1 placeholder book even if user has no books
  var displayBooks = books.length > 0 ? books : [null];

  displayBooks.forEach(function(book) {
    var card = document.createElement("div");
    card.className = "book-card";

    if (book) {
      var cover = document.createElement("div");
      cover.className = "book-cover";

      var img = document.createElement("img");
      img.src = book.coverUrl;
      img.alt = book.title;
      img.onerror = function() {
        cover.classList.add("empty");
        cover.innerHTML =
          '<svg width="48" height="48" fill="none" stroke="#999" stroke-width="1.2" viewBox="0 0 24 24">' +
          '<rect x="3" y="2" width="13" height="20" rx="1"/>' +
          '<path d="M7 6h5M7 10h5M7 14h3"/>' +
          '</svg>';
      };
      cover.appendChild(img);

      var title = document.createElement("div");
      title.className = "book-title";
      title.textContent = book.title;

      var author = document.createElement("div");
      author.className = "book-author";
      author.textContent = book.author;

      card.appendChild(cover);
      card.appendChild(title);
      card.appendChild(author);

    } else {
      // placeholder book
      card.innerHTML =
        '<div class="book-cover empty">' +
          '<svg width="48" height="48" fill="none" stroke="#999" stroke-width="1.2" viewBox="0 0 24 24">' +
          '<rect x="3" y="2" width="13" height="20" rx="1"/>' +
          '<path d="M7 6h5M7 10h5M7 14h3"/>' +
          '</svg>' +
        '</div>' +
        '<div class="book-title" style="color:#bbb;">—</div>' +
        '<div class="book-author" style="color:#ccc;">No books yet</div>';
    }

    track.appendChild(card);
  });

  // page dots
  var pages = Math.max(1, Math.ceil(displayBooks.length / VISIBLE));
  for (var p = 0; p < pages; p++) {
    (function(pageIndex) {
      var dot = document.createElement("div");
      dot.className = "dot" + (pageIndex === 0 ? " active" : "");
      dot.addEventListener("click", function() { goTo(pageIndex); });
      dotsWrap.appendChild(dot);
    })(p);
  }

  updateCarousel();
}

/* -carousel navigation- */
function goTo(page) {
  var displayBooks = currentUser.books.length > 0 ? currentUser.books : [null];
  var pages = Math.ceil(displayBooks.length / VISIBLE);
  currentIndex = Math.max(0, Math.min(page, pages - 1));
  updateCarousel();
}

function updateCarousel() {
  var track        = document.getElementById("booksTrack");
  var dots         = document.querySelectorAll(".dot");
  var displayBooks = currentUser.books.length > 0 ? currentUser.books : [null];
  var pages        = Math.ceil(displayBooks.length / VISIBLE);

  track.style.transform = "translateX(-" + (currentIndex * VISIBLE * CARD_W) + "px)";

  dots.forEach(function(dot, i) {
    dot.classList.toggle("active", i === currentIndex);
  });

  document.getElementById("prevBtn").disabled = currentIndex === 0;
  document.getElementById("nextBtn").disabled = currentIndex >= pages - 1;
}

document.getElementById("prevBtn").addEventListener("click", function() {
  goTo(currentIndex - 1);
});

document.getElementById("nextBtn").addEventListener("click", function() {
  goTo(currentIndex + 1);
});

/* render 'init' */
renderProfile(currentUser);