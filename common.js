const d3 = require("d3");

const svgWidth = 560,
    svgHeight = 400,
    svgMargin = { top: 40, right: 20, bottom: 20, left: 40 },
    xLeftBoundary = -100,
    xRightBoundary = 100,
    yBottomBoundary = -100,
    yTopBoundary = 100,
    pointRadius = 3;

const xScale = d3.scaleLinear()  //this thing transform points coordinates to svg coordinates
    .domain([ xLeftBoundary, xRightBoundary ])    //boundaries of points' x coordinates
    .range([svgMargin.left, svgWidth - svgMargin.right]);   //boundaries of svg's x coordinates

const yScale = d3.scaleLinear()
    .domain([ yBottomBoundary, yTopBoundary ])
    .range([svgHeight - svgMargin.bottom, svgMargin.top]); //inverted to invert screen y axis

function initAxes(svg) {
    // Add an X and an Y Axis
    const xAxis = d3.axisBottom(xScale)
        .tickSizeInner(-(svgHeight-svgMargin.top-svgMargin.bottom)) //grid lines
        .tickSizeOuter(0)
        .tickPadding(10);

    const yAxis = d3.axisLeft(yScale)
        .tickSizeInner(-(svgWidth-svgMargin.left-svgMargin.right))  //grid lines
        .tickSizeOuter(0)
        .tickPadding(10);

    // Adds X-Axis to svg as a 'g' element (group)
    svg.append("g")
        .attr("class", "axis")  // Give class so we can style it
        .attr("transform", "translate(" + [0, svgHeight - svgMargin.bottom] + ")")  // Translate just moves it down into position (or will be on top)
        .call(xAxis);

    // Adds Y-Axis to svg as a 'g' element (group)
    svg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + [svgMargin.left, 0] + ")")
        .call(yAxis);
}

module.exports = {
    svgWidth,
    svgHeight,
    svgMargin,
    xLeftBoundary,
    xRightBoundary,
    yBottomBoundary,
    yTopBoundary,
    pointRadius,
    xScale,
    yScale,
    initAxes
};
