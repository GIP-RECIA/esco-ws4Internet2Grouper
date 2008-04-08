var debug = false;

/**
* Simule a click for a link.
*/
function simulateLinkClick(linkId) {
	var fireOnThis = document.getElementById(linkId)
    if (fireOnThis == null) {
      if (debug) alert("element [" + linkId + "] not found");
      return;
    }
	if (document.createEvent) {
		// Firefox
		var evObj = document.createEvent('MouseEvents');
		evObj.initEvent( 'click', true, false );
		fireOnThis.dispatchEvent(evObj);
	} else if (document.createEventObject) {
		// IE
		fireOnThis.fireEvent('onclick');
		// IE 7
		fireOnThis.click();
	}
}

/** 
 * @return the body element of a table.
 */
function getTableBody(table) {
  var children = table.childNodes;
  for (i = 0; i < children.length; i++) {
    var child = children[i];
    var childTagName = child.tagName;
    if (childTagName != null && childTagName.toLowerCase() == "tbody") {
      return child;
    }
  }
  return null;
}

/**
* HighLight table rows.
*/
function highlightTableRows(tableId) {
    var table = document.getElementById(tableId); 
    if (table == null) {
      if (debug) alert("table [" + tableId + "] not found");
      return;
    }
    var tbody = getTableBody(table);
    if (tbody == null) {
      if (debug) alert("body of table [" + tableId + "] not found");
      return;
    }
    var rows = tbody.getElementsByTagName("tr");
    if (rows == null) {
      if (debug) alert("no row found in the body of table [" + tableId + "]");
      return;
    }
    var previousClass = null;
    // add event handlers so rows light up
    for (i = 0; i < rows.length; i++) {
        rows[i].onmouseover = function() { previousClass = this.className; this.className = 'portlet-table-selected'; };
        rows[i].onmouseout = function() { this.className = previousClass; };
    }
}

/*
 * Hide a button.
 */
function hideButton(buttonId){
	var button = document.getElementById(buttonId);
	if (button != null) {
  	  button.style.visibility = "hidden";
  	  button.style.width = 0;
	} else if (debug) 
      alert("button [" + buttonId + "] not found");
}

/*
 * Hide an element.
 */
function hideElement(id){
	var element = document.getElementById(id);
	if (element != null) {
  	  element.style.display = "none";
	} else if (debug) 
      alert("element [" + id + "] not found");
}

/*
 * Show an element.
 */
function showElement(id){
	var element = document.getElementById(id);
	if (element != null) {
  	  element.style.display = "block";
	} else if (debug) 
      alert("element [" + id + "] not found");
}

/*
 * Hide buttons in a table.
 */
function hideTableButtons(tableId, buttonId){
    var table = document.getElementById(tableId);
    if (table == null) {
      if (debug) alert("table [" + tableId + "] not found");
      return;
    }
    var tbody = getTableBody(table);
    if (tbody == null) {
      if (debug) alert("body of table [" + tableId + "] not found");
      return;
    }
    var rows = tbody.getElementsByTagName("tr");
    if (rows == null) {
      if (debug) alert("no row found in the body of table [" + tableId + "]");
      return;
    }
    // hide button for each row.
    for (i=0; i < rows.length; i++) {
      hideButton(tableId + ":" + i + ":" + buttonId);
 	}
}

/**
 * focus an element.
 */
function focusElement(id) {
	var element = document.getElementById(id);
	if (element != null) {
		element.focus();
	} else if (debug) {
      alert("element [" + id + "] not found, could not focus");
	}
}

/**
 * enable/disable an element.
 */
function enableElement(id, setEnabled) {
	var element = document.getElementById(id);
	if (element != null) {
		element.disabled = !setEnabled;
	} else if (debug) {
      alert("element [" + id + "] not found, could not enable/disable");
    }
}

/**
 * enable/disable an element.
 */
function isChecked(id) {
	var element = document.getElementById(id);
	if (element != null) {
		return element.checked;
	} else if (debug) {
      alert("element [" + id + "] not found");
    }
}

