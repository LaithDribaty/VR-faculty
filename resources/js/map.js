var width = window.innerWidth-15;
var height = window.innerHeight/1.5;

var stage = new Konva.Stage({
    container: 'container',
    width: width,
    height: height,
});

var layer = new Konva.Layer();
stage.add(layer);

var WIDTH = 3000;
var HEIGHT = 3000;

// now draw our bars
var scrollLayers = new Konva.Layer();
stage.add(scrollLayers);

const PADDING = 3;

var verticalBar = new Konva.Rect({
    width: 10,
    height: 100,
    fill: 'grey',
    opacity: 0.8,
    x: stage.width() - PADDING - 10,
    y: PADDING,
    draggable: true,
    dragBoundFunc: function (pos) {
    pos.x = stage.width() - PADDING - 10;
    pos.y = Math.max(
        Math.min(pos.y, stage.height() - this.height() - PADDING),
        PADDING
    );
    return pos;
    },
});
scrollLayers.add(verticalBar);

verticalBar.on('dragmove', function () {
    // delta in %
    const availableHeight =
    stage.height() - PADDING * 2 - verticalBar.height();
    var delta = (verticalBar.y() - PADDING) / availableHeight;

    layer.y(-(HEIGHT - stage.height()) * delta);
});

var horizontalBar = new Konva.Rect({
    width: 100,
    height: 10,
    fill: 'grey',
    opacity: 0.8,
    x: PADDING,
    y: stage.height() - PADDING - 10,
    draggable: true,
    dragBoundFunc: function (pos) {
    pos.x = Math.max(
        Math.min(pos.x, stage.width() - this.width() - PADDING),
        PADDING
    );
    pos.y = stage.height() - PADDING - 10;

    return pos;
    },
});
scrollLayers.add(horizontalBar);

horizontalBar.on('dragmove', function () {
    // delta in %
    const availableWidth =
    stage.width() - PADDING * 2 - horizontalBar.width();
    var delta = (horizontalBar.x() - PADDING) / availableWidth;

    layer.x(-(WIDTH - stage.width()) * delta);
});

stage.on('wheel', function (e) {
    // prevent parent scrolling
    e.evt.preventDefault();
    if(e.evt.shiftKey) {
        const dx = e.evt.deltaY;
        const dy = e.evt.deltaX;

        const minX = -(WIDTH - stage.width());
        const maxX = 0;

        const x = Math.max(minX, Math.min(layer.x() - dx, maxX));

        const minY = -(HEIGHT - stage.height());
        const maxY = 0;

        const y = Math.max(minY, Math.min(layer.y() - dy, maxY));
        layer.position({ x, y });

        const availableHeight =
        stage.height() - PADDING * 2 - verticalBar.height();
        const vy =
        (layer.y() / (-HEIGHT + stage.height())) * availableHeight + PADDING;
        verticalBar.y(vy);

        const availableWidth =
        stage.width() - PADDING * 2 - horizontalBar.width();

        const hx =
        (layer.x() / (-WIDTH + stage.width())) * availableWidth + PADDING;
        horizontalBar.x(hx);
    } else {
        const dx = e.evt.deltaX;
        const dy = e.evt.deltaY;

        const minX = -(WIDTH - stage.width());
        const maxX = 0;

        const x = Math.max(minX, Math.min(layer.x() - dx, maxX));

        const minY = -(HEIGHT - stage.height());
        const maxY = 0;

        const y = Math.max(minY, Math.min(layer.y() - dy, maxY));
        layer.position({ x, y });

        const availableHeight =
        stage.height() - PADDING * 2 - verticalBar.height();
        const vy =
        (layer.y() / (-HEIGHT + stage.height())) * availableHeight + PADDING;
        verticalBar.y(vy);

        const availableWidth =
        stage.width() - PADDING * 2 - horizontalBar.width();

        const hx =
        (layer.x() / (-WIDTH + stage.width())) * availableWidth + PADDING;
        horizontalBar.x(hx);            
    }
});

var currentElement;

let currentShape;
var menuNode = document.getElementById("menu");
var deleted = [];

function change(element)
{
    currentElement = element.getAttribute('option');
}

stage.on('click', (e)=>{
    e.evt.preventDefault();
    if(e.evt.which != 1) {
        return 
    }
    if(currentElement){
        e.evt.preventDefault();
        var mousePos = stage.getPointerPosition();
        var tempText = new Konva.Text({
            x: mousePos.x-layer.x(),
            y: mousePos.y-layer.y(),
            fontSize: 30,
            text: currentElement,
            draggable: true,
        });
        layer.add(tempText);

        var tempHandler = new Konva.Transformer({
            nodes: [tempText],
            centeredScaling: true,
            rotationSnaps: [0, 90, 180, 270],
            resizeEnabled: true,
        });
        layer.add(tempHandler);
    }

    menuNode.style.display = "none";
});

document.getElementById("delete-button").addEventListener("click", () => {
  const tr = layer
    .find("Transformer")
    .find((tr) => tr.nodes()[0] === currentShape);
  deleted.push(currentShape.attrs.id);
  tr.destroy();
  currentShape.destroy();
  layer.draw();
  menuNode.style.display = "none";
});

stage.on("contextmenu", function (e) {
    // prevent default behavior
    e.evt.preventDefault();
    if (e.target === stage) {
      // if we are on empty place of the stage we will do nothing
      return;
    }
    currentShape = e.target;

    // show menu
    menuNode.style.display = "initial";
    var containerRect = stage.container().getBoundingClientRect();
    menuNode.style.top = containerRect.top + stage.getPointerPosition().y + 4 + "px";
    menuNode.style.left = containerRect.left + stage.getPointerPosition().x + 4 + "px";
});


document.getElementById("saveBtn").addEventListener("click", (e) => {
    let yes = confirm('are you sure you want to save');
    if(!yes) return ;

    let dataArr = [];
    for(let i=0; i<stage.children[0].children.length; ++i) {
        if( stage.children[0].children[i].getType() == 'Shape' ) {
            let el = {
                'id': stage.children[0].children[i].attrs.id,
                'name': stage.children[0].children[i].attrs.text,
                'position': {
                    'x': stage.children[0].children[i].attrs.x,
                    'y': stage.children[0].children[i].attrs.y
                },
                'scale': {
                    'x': stage.children[0].children[i].attrs.scaleX,
                    'y': stage.children[0].children[i].attrs.scaleY
                },
                'rotation': {
                    'z': stage.children[0].children[i].attrs.rotation
                }
            }
            dataArr.push(el);
        }
    }

    $.ajax({
        url: editUrl,
        method: "PUT",
        data: {
            'data': dataArr,
            'deleted': deleted
        }
    }).done(function() {
        alert('save completed');
        deleted = [];
    });
});


for(let i=0;i<alreadyExist.length;++i){
    var tempText = new Konva.Text({
        id: alreadyExist[i].id,
        x: alreadyExist[i].position.x,
        y: alreadyExist[i].position.y,
        scaleX: alreadyExist[i].scale.x,
        scaleY: alreadyExist[i].scale.y,
        rotation: alreadyExist[i].rotation.z,
        fontSize: 30,
        text: alreadyExist[i].name,
        draggable: true,
    });
    layer.add(tempText);

    var tempHandler = new Konva.Transformer({
        nodes: [tempText],
        centeredScaling: true,
        rotationSnaps: [0, 90, 180, 270],
        resizeEnabled: true,
    });
    layer.add(tempHandler);
}