import os, subprocess, shutil
from PIL import Image
from collections import deque

def is_mp4(path):
    with open(path, "rb") as f:
        return b"ftyp" in f.read(32)

def is_light(r, g, b):
    return (max(r, g, b) - min(r, g, b)) < 36 and (r + g + b) // 3 > 145

def clear(im):
    im = im.convert("RGBA")
    w, h = im.size
    pix = im.load()
    rem = bytearray(w * h)
    for y in range(h):
        for x in range(w):
            r, g, b, a = pix[x, y]
            if a > 10 and is_light(r, g, b):
                rem[y * w + x] = 1
    q = deque()
    def enq(x, y):
        if 0 <= x < w and 0 <= y < h and rem[y * w + x]:
            rem[y * w + x] = 0
            q.append((x, y))
    for x in range(w):
        enq(x, 0); enq(x, h - 1)
    for y in range(h):
        enq(0, y); enq(w - 1, y)
    while q:
        x, y = q.popleft()
        r, g, b, a = pix[x, y]
        pix[x, y] = (r, g, b, 0)
        enq(x + 1, y); enq(x - 1, y); enq(x, y + 1); enq(x, y - 1)
    for _ in range(6):
        kill = []
        for y in range(1, h - 1):
            for x in range(1, w - 1):
                r, g, b, a = pix[x, y]
                if a == 0 or not is_light(r, g, b):
                    continue
                hole = (pix[x, y - 1][3] == 0) + (pix[x, y + 1][3] == 0) + (pix[x - 1, y][3] == 0) + (pix[x + 1, y][3] == 0)
                if hole >= 2:
                    kill.append((x, y))
        for x, y in kill:
            r, g, b, a = pix[x, y]
            pix[x, y] = (r, g, b, 0)
    return im

def save_png(path):
    im = clear(Image.open(path))
    box = im.split()[-1].getbbox()
    if box:
        im = im.crop(box)
    im.save(path, "PNG")
    print("png", path, im.size)

def video_to_gif(path):
    if not is_mp4(path):
        print("skip", path)
        return
    work = path + ".frames"
    if os.path.isdir(work):
        shutil.rmtree(work)
    os.makedirs(work)
    subprocess.check_call(["ffmpeg", "-y", "-i", path, "-vf", "fps=10,scale=420:-1:flags=lanczos", os.path.join(work, "f%04d.png")])
    names = sorted(n for n in os.listdir(work) if n.startswith("f"))
    cleaned, box = [], None
    for name in names:
        im = clear(Image.open(os.path.join(work, name)))
        b = im.split()[-1].getbbox()
        cleaned.append(im)
        if b:
            if box is None:
                box = list(b)
            else:
                box[0] = min(box[0], b[0]); box[1] = min(box[1], b[1])
                box[2] = max(box[2], b[2]); box[3] = max(box[3], b[3])
    if box:
        cleaned = [im.crop(tuple(box)) for im in cleaned]
    for i, im in enumerate(cleaned):
        im.save(os.path.join(work, "c%04d.png" % (i + 1)))
    out = path + ".out.gif"
    subprocess.check_call(["ffmpeg", "-y", "-framerate", "10", "-i", os.path.join(work, "c%04d.png"), "-vf", "split[s0][s1];[s0]palettegen=reserve_transparent=1[p];[s1][p]paletteuse=alpha_threshold=128", "-loop", "0", out])
    os.replace(out, path)
    shutil.rmtree(work)
    print("gif", path, os.path.getsize(path))

raw = "app/src/main/res/raw"
draw = "app/src/main/res/drawable"
for name in ("press.gif", "loading.gif"):
    p = os.path.join(raw, name)
    if os.path.exists(p):
        video_to_gif(p)
for name in ("vpn_button.png", "vpn_connected.png"):
    p = os.path.join(draw, name)
    if os.path.exists(p):
        save_png(p)
