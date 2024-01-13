function gamesScatter(key) {
    const outerHeight = 300;
    const outerWidth = 300;
    const margin = {top: 20, right: 20, bottom: 40, left: 40};
    const height = outerHeight - (margin.top + margin.bottom);
    const width = outerWidth - (margin.left + margin.right);
    fetch(`/teamGames?key=${key}`).then(response => {
        return response.json();
    }).then(resp => {
        let data = resp.data;
        let svg = d3.select("#scatterPlot").append("svg")
            .attr("width", outerWidth)
            .attr("height", outerHeight);

        let maxAxis = d3.max(data, d => Math.max(d.score + 5, d.oppScore + 5));
        let minAxis = Math.max(0, d3.min(data, d => Math.min(d.score - 5, d.oppScore - 5)));
        let xScale = d3.scaleLinear().domain([minAxis, maxAxis]).range([0, width]);
        let yScale = d3.scaleLinear().domain([minAxis, maxAxis]).range([height, 0]);

        svg.selectAll("circle")
            .data(data)
            .enter()
            .append("circle")
            .attr("cx", (d, i) => xScale(d.oppScore))
            .attr("cy", (d, i) => yScale(d.score))
            .attr("transform","translate("+margin.left+","+margin.top+")")
            .attr("r", (d, i) => 5)
            .attr("fill", (d,i)=> "#"+resp.color);

        svg.append("line")
            .attr("x1",margin.left)
            .attr("x2", margin.left+width)
            .attr("y1", margin.top+height)
            .attr("y2", margin.top)
            .attr("stroke", "gray")
            .attr("stroke-width", 1)
            .attr("stroke-dasharray", "4 1");
        // Add the X Axis
        svg.append("g")
            .attr("transform", `translate(${margin.left}, ${margin.top + height})`)
            .call(d3.axisBottom(xScale).ticks(10));

        // Add the Y Axis
        svg.append("g")
            .attr("transform", `translate(${margin.left}, ${margin.top})`)
            .call(d3.axisLeft(yScale).ticks(10));

        // Add the y-axis title
        let fontPx=14;
        let labelX = margin.left/2-(fontPx/1.5);
        let labelY = height/2+margin.top;
        svg.append("text")
            .attr("x", labelX)
            .attr("y", labelY)
            .style("writing-mode", "tb")
            .style("text-anchor", "middle")
            .style("font-family", "Bevan")
            .style("font-size", fontPx+"px")
            .text(resp.shortName);


        let oppLabelX = width/2+margin.left;
        let oppLabelY = height+margin.top+margin.bottom-fontPx/1.5;
        svg.append("text")
            .attr("x", oppLabelX)
            .attr("y", oppLabelY)
            .style("text-anchor", "middle")
            .style("font-family", "Bevan")
            .style("font-size", fontPx+"px")
            .text("Opponent");

        svg.append("text")
            .attr("x", 260)
            .attr("y",30)
            .attr("fill", "gray")
            .style("text-anchor", "end")
            .style("font-family", "Arial")
            .style("font-size", "8px")
            .text("Wins");
        svg.append("text")
            .attr("x", 290)
            .attr("y",50)
            .attr("fill", "gray")
            .style("text-anchor", "end")
            .style("font-family", "Arial")
            .style("font-size", "8px")
            .text("Losses");
    })
}